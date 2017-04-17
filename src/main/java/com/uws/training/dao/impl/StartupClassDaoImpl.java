/**   
* @Title: StartupClassDaoImpl.java 
* @Package com.uws.training.dao.impl 
* @Description: TODO(用一句话描述该文件做什么) 
* @author zhangyb   
* @date 2015年10月21日 下午1:36:55 
* @version V1.0   
*/
package com.uws.training.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.uws.core.hibernate.dao.impl.BaseDaoImpl;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.util.DataUtil;
import com.uws.core.util.HqlEscapeUtil;
import com.uws.domain.training.StartupClassApplyInfo;
import com.uws.domain.training.StartupClassInfo;
import com.uws.sys.model.Dic;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.impl.DicFactory;
import com.uws.training.dao.IStartupClassDao;

/** 
 * @ClassName: StartupClassDaoImpl 
 * @Description: TODO 创业班管理DAO
 * @author zhangyb 
 * @date 2015年10月21日 下午1:36:55  
 */
@Repository("startupClassDao")
public class StartupClassDaoImpl extends BaseDaoImpl implements
		IStartupClassDao {
	@Autowired
	private DicUtil dicUtil = DicFactory.getDicUtil();
	@Override
	public Page queryStartupClassPage(StartupClassInfo startupClassInfo,
			int pageNo, int pageSize, String flag) {
		String save = dicUtil.getDicInfo("STARTUP_CLASS_STATUS", "SAVED").getId();
		List<String> values = new ArrayList<String>();
		StringBuffer hql = new StringBuffer("from StartupClassInfo s where 1=1");
//		班级名称
		if(DataUtil.isNotNull(startupClassInfo.getGrowthClassName())) {
			hql.append(" and s.growthClassName like ?");
			values.add("%" + HqlEscapeUtil.escape(startupClassInfo.getGrowthClassName()) + "%");
		}
//		班级类型
		if(startupClassInfo.getGrowthClassType() != null && DataUtil.isNotNull(startupClassInfo.getGrowthClassType().getCode())) {
			hql.append(" and s.growthClassType.code = ?");
			values.add(startupClassInfo.getGrowthClassType().getCode());
		}
//		班级状态
		if(startupClassInfo.getGrowthClassStatus() != null && DataUtil.isNotNull(startupClassInfo.getGrowthClassStatus().getCode())) {
			hql.append(" and s.growthClassStatus.code = ?");
			values.add(startupClassInfo.getGrowthClassStatus().getCode());
		}
//		学年
		if(startupClassInfo.getSchoolYear() != null && DataUtil.isNotNull(startupClassInfo.getSchoolYear().getCode())) {
			hql.append(" and s.schoolYear.code = ?");
			values.add(startupClassInfo.getSchoolYear().getCode());
		}
		//班主任操作数据过滤
		if(flag.equalsIgnoreCase("hm")) {
			if(DataUtil.isNotNull(startupClassInfo) && DataUtil.isNotNull(startupClassInfo.getGrowthClassLeader())) {
				hql.append(" and s.growthClassLeader.id = ?");
				values.add(startupClassInfo.getGrowthClassLeader().getId());
			}
			hql.append(" and s.growthClassStatus.id != ?");
			values.add(save);
		}
		hql.append(" order by updateTime desc");
		return this.pagedQuery(hql.toString(),pageNo,pageSize,values.toArray());
	}

	@Override
	public void saveStartupClass(StartupClassInfo startupClassInfo,
			String[] fileId) {
		save(startupClassInfo);
	}

	@Override
	public void updateStartupClass(StartupClassInfo startupClassInfo,
			String[] fileId) {
		update(startupClassInfo);
	}

	@Override
	public void deleteStartupClass(StartupClassInfo startupClassInfo) {
		delete(startupClassInfo);
	}

	/* (非 Javadoc) 
	* <p>Title: getClassInfoById</p> 
	* <p>Description: </p> 
	* @param classInfoId
	* @return 
	* @see com.uws.training.dao.IStartupClassDao#getClassInfoById(java.lang.String) 
	*/
	@Override
	public StartupClassInfo getClassInfoById(String classInfoId) {
		String sql = "from StartupClassInfo c where c.id = ?";
		return (StartupClassInfo) this.queryUnique(sql.trim(), new Object[]{classInfoId});
	}

	/* (非 Javadoc) 
	* <p>Title: getClassInfoListByName</p> 
	* <p>Description: </p> 
	* @param className
	* @return 
	* @see com.uws.training.dao.IStartupClassDao#getClassInfoListByName(java.lang.String) 
	*/
	@SuppressWarnings("unchecked")
	@Override
	public List<StartupClassInfo> getClassInfoListByName(String className) {
		String hql = " from StartupClassInfo c where 1 = 1 and  c.growthClassName = '" + className + "'";
		System.out.println(hql);
		return this.query(hql, new Object[]{});
	}

	/* (非 Javadoc) 
	* <p>Title: queryStuApplyPage</p> 
	* <p>Description: </p> 
	* @param startupClassInfo
	* @param pageNo
	* @param pageSize
	* @return 
	* @see com.uws.training.dao.IStartupClassDao#queryStuApplyPage(com.uws.domain.training.StartupClassInfo, int, int) 
	*/
	@Override
	public Page queryStuApplyPage(StartupClassInfo startupClassInfo,
			int pageNo, int pageSize, String userId, Dic applyStatus,Dic approveStatus) {
		
		List<String> values = new ArrayList<String>();
		Dic openClass = this.dicUtil.getDicInfo("STARTUP_CLASS_STATUS", "OPENING");
		StringBuffer hql = new StringBuffer("select yearDic.name as c1,s.growth_class_name as c2,typeDic.name as c3,"
				+ "s.growth_class_theme as c4,teacher.name as c5,applyDic.Name as c6,s.id as c7, a.id as c8, approveDic.name as c9,"
				+ "classStatusDic.name  as c10 "
				+ "from hky_training_startup_applyinfo a right join hky_training_startup_info s"
				+ " on (a.growth_id = s.id and a.student_id = ?)"
				+ " left join dic approveDic on a.APPROVE_STATUS = approveDic.id"
				+ " left join dic  yearDic on s.school_year = yearDic.id"
				+ " left join dic  typeDic on s.growth_class_type = typeDic.id"
				+ " left join dic  classStatusDic on s.growth_class_status = classStatusDic.id"
				+ " left join Hky_Base_Teacher teacher on s.growth_class_leader = teacher.id"
				+ " left join dic applyDic on a.apply_status = applyDic.Id where 1 = 1 "
				+ " and (s.GROWTH_CLASS_STATUS = ? or s.GROWTH_CLASS_STATUS = ?) and s.school_year = ?");
		values.add(userId);
		values.add(startupClassInfo.getGrowthClassStatus().getId());
		values.add(openClass.getId());
		values.add(startupClassInfo.getSchoolYear().getId());
//		班级名称
		if(DataUtil.isNotNull(startupClassInfo.getGrowthClassName())) {
			hql.append(" and s.growth_class_name like ?");
			values.add("%" + HqlEscapeUtil.escape(startupClassInfo.getGrowthClassName()) + "%");
		}
//		班级类型
		if(startupClassInfo.getGrowthClassType() != null && DataUtil.isNotNull(startupClassInfo.getGrowthClassType().getId())) {
			hql.append(" and s.growth_class_type = ?");
			values.add(startupClassInfo.getGrowthClassType().getId());
		}
//		报名状态
		if(applyStatus != null && DataUtil.isNotNull(applyStatus.getId())) {
			Dic dic = this.dicUtil.getDicInfo("STARTUP_APPLY_STATUS", "UNAPPLY");
			if(dic.getId().equals(applyStatus.getId())) {
				hql.append(" and a.apply_status is null");
			}else{
				hql.append(" and a.apply_status = ?");
				values.add(applyStatus.getId());
			}
		}
//		审核状态
		if(approveStatus != null && DataUtil.isNotNull(approveStatus.getCode())) {
			Dic approveDic = this.dicUtil.getDicInfo("STARTUP_APPROVE_STATUS", approveStatus.getCode());
			hql.append(" and a.approve_status = ?");
			values.add(approveDic.getId());
		}
		return this.pagedSQLQuery(hql.toString(), pageNo, pageSize, values.toArray());
	}

	/* (非 Javadoc) 
	* <p>Title: saveStuApplyInfo</p> 
	* <p>Description: </p> 
	* @param applyInfo 
	* @see com.uws.training.dao.IStartupClassDao#saveStuApplyInfo(com.uws.domain.training.StartupClassApplyInfo) 
	*/
	@Override
	public void saveStuApplyInfo(StartupClassApplyInfo applyInfo) {
		this.save(applyInfo);
	}

	/* (非 Javadoc) 
	* <p>Title: updateStuApplyInfo</p> 
	* <p>Description: </p> 
	* @param applyInfo 
	* @see com.uws.training.dao.IStartupClassDao#updateStuApplyInfo(com.uws.domain.training.StartupClassApplyInfo) 
	*/
	@Override
	public void updateStuApplyInfo(StartupClassApplyInfo applyInfo) {
		this.update(applyInfo);
	}

	/* (非 Javadoc) 
	* <p>Title: delStuApplyInfo</p> 
	* <p>Description: </p> 
	* @param applyInfo 
	* @see com.uws.training.dao.IStartupClassDao#delStuApplyInfo(com.uws.domain.training.StartupClassApplyInfo) 
	*/
	@Override
	public void delStuApplyInfo(StartupClassApplyInfo applyInfo) {
		this.delete(applyInfo);
	}

	/* (非 Javadoc) 
	* <p>Title: getStuApplyInfoById</p> 
	* <p>Description: </p> 
	* @param id
	* @return 
	* @see com.uws.training.dao.IStartupClassDao#getStuApplyInfoById(java.lang.String) 
	*/
	@Override
	public StartupClassApplyInfo getStuApplyInfoById(String id) {
		String hql = " from StartupClassApplyInfo s where s.id = ?";
		return (StartupClassApplyInfo) this.queryUnique(hql, new Object[]{id});
	}

	/* (非 Javadoc) 
	* <p>Title: queryStuApprovePage</p> 
	* <p>Description: </p> 
	* @param applyInfo
	* @param pageNo
	* @param pageSize
	* @return 
	* @see com.uws.training.dao.IStartupClassDao#queryStuApprovePage(com.uws.domain.training.StartupClassApplyInfo, int, int) 
	*/
	@Override
	public Page queryStuApprovePage(StartupClassApplyInfo applyInfo,
			int pageNo, int pageSize, String userId) {
		List<String> values = new ArrayList<String>();
		StringBuffer hql = new StringBuffer(" from StartupClassApplyInfo a where 1 = 1");
//		学院
		if(DataUtil.isNotNull(applyInfo.getStudentId()) 
				&& DataUtil.isNotNull(applyInfo.getStudentId().getCollege())
				&& DataUtil.isNotNull(applyInfo.getStudentId().getCollege().getId())) {
			hql.append(" and a.studentId.college.id = ?");
			values.add(applyInfo.getStudentId().getCollege().getId());
		}
//		专业
		if(DataUtil.isNotNull(applyInfo.getStudentId()) 
				&& DataUtil.isNotNull(applyInfo.getStudentId().getMajor())
				&& DataUtil.isNotNull(applyInfo.getStudentId().getMajor().getId())) {
			hql.append(" and a.studentId.major.id = ?");
			values.add(applyInfo.getStudentId().getMajor().getId());
		}
//		班级
		if(DataUtil.isNotNull(applyInfo.getStudentId()) 
				&& DataUtil.isNotNull(applyInfo.getStudentId().getClassId())
				&& DataUtil.isNotNull(applyInfo.getStudentId().getClassId().getId())) {
			hql.append(" and a.studentId.classId.id = ?");
			values.add(applyInfo.getStudentId().getClassId().getId());
		}
//		姓名
		if(DataUtil.isNotNull(applyInfo.getStudentId()) 
				&& DataUtil.isNotNull(applyInfo.getStudentId().getName())) {
			hql.append(" and a.studentId.name like ?");
			values.add("%" + HqlEscapeUtil.escape(applyInfo.getStudentId().getName()) + "%");
		}
//		学号
		if(DataUtil.isNotNull(applyInfo.getStudentId()) 
				&& DataUtil.isNotNull(applyInfo.getStudentId().getStuNumber())) {
			hql.append(" and a.studentId.stuNumber like ?");
			values.add("%" + HqlEscapeUtil.escape(applyInfo.getStudentId().getStuNumber()) + "%");
		}
//		创业班类型
		if(DataUtil.isNotNull(applyInfo) && DataUtil.isNotNull(applyInfo.getGrowthId())
				&& DataUtil.isNotNull(applyInfo.getGrowthId().getGrowthClassType())
				&& DataUtil.isNotNull(applyInfo.getGrowthId().getGrowthClassType().getCode())) {
			hql.append(" and a.growthId.growthClassType.code = ?");
			values.add(applyInfo.getGrowthId().getGrowthClassType().getCode());
		}
//		结业状态
		if(applyInfo.getCompleteStatus() != null && DataUtil.isNotNull(applyInfo.getCompleteStatus().getCode())) {
			hql.append(" and a.completeStatus.code = ?");
			values.add(applyInfo.getCompleteStatus().getCode());
		}
//		审核状态
		if(applyInfo.getApproveStatus() != null && DataUtil.isNotNull(applyInfo.getApproveStatus().getId())) {
			hql.append(" and a.approveStatus.id = ?");
			values.add(applyInfo.getApproveStatus().getId());
		}
//		学年
		if(applyInfo.getGrowthId() != null && applyInfo.getGrowthId().getSchoolYear() != null
				&& DataUtil.isNotNull(applyInfo.getGrowthId().getSchoolYear().getCode())) {
			hql.append(" and a.growthId.schoolYear.code = ?");
			values.add(applyInfo.getGrowthId().getSchoolYear().getCode());
		}
//		创业班名称
		if(DataUtil.isNotNull(applyInfo) && DataUtil.isNotNull(applyInfo.getGrowthId())
				&& DataUtil.isNotNull(applyInfo.getGrowthId().getGrowthClassName())) {
			hql.append(" and a.growthId.growthClassName like ?");
			values.add("%" + HqlEscapeUtil.escape(applyInfo.getGrowthId().getGrowthClassName()) + "%");
		}
//		创业班状态
		if(applyInfo != null && applyInfo.getGrowthId() != null && applyInfo.getGrowthId().getGrowthClassStatus() != null
				&& DataUtil.isNotNull(applyInfo.getGrowthId().getGrowthClassStatus().getCode())) {
			hql.append(" and a.growthId.growthClassStatus.code = ?");
			values.add(applyInfo.getGrowthId().getGrowthClassStatus().getCode());
		}
		//过滤条件
		if(DataUtil.isNotNull(userId)) {
			hql.append(" and a.growthId.growthClassLeader.id = ?");
			values.add(userId);
			//结业列表查询的时候set一个审核通过状态，通过审核状态判断是否要加该过滤条件
			if(DataUtil.isNotNull(applyInfo) && DataUtil.isNotNull(applyInfo.getApproveStatus()) && DataUtil.isNotNull(applyInfo.getApproveStatus().getId())) {
				hql.append(" and a.approveStatus.id = ?");
				values.add(applyInfo.getApproveStatus().getId());
			}
		}else if(DataUtil.isNotNull(applyInfo) && DataUtil.isNotNull(applyInfo.getGrowthId()) && DataUtil.isNotNull(applyInfo.getGrowthId().getId())) {
			hql.append(" and a.growthId.id = ?");
			values.add(applyInfo.getGrowthId().getId());
		}
		return this.pagedQuery(hql.toString(), pageNo, pageSize, values.toArray());
	}

	/* (非 Javadoc) 
	* <p>Title: getSubClassInfoList</p> 
	* <p>Description: </p> 
	* @return 
	* @see com.uws.training.dao.IStartupClassDao#getSubClassInfoList() 
	*/
	@SuppressWarnings("unchecked")
	@Override
	public List<StartupClassInfo> getSubClassInfoList(Dic classStatus, String userId) {
		//获得不是已保存和已结班的创业班
		Dic dic = this.dicUtil.getDicInfo("STARTUP_CLASS_STATUS", "CLOSED");
		if(DataUtil.isNotNull(userId)) {
			String hql = " from StartupClassInfo s where s.growthClassStatus.code != ? and s.growthClassStatus.code != ? and s.growthClassLeader.id =? ";
			return this.query(hql, new Object[]{classStatus.getCode(),dic.getCode(), userId});
		}else {
			String hql = " from StartupClassInfo s where s.growthClassStatus.code != ? and s.growthClassStatus.code != ?";
			return this.query(hql, new Object[]{classStatus.getCode(),dic.getCode()});
		}
		
	}

	/* (非 Javadoc) 
	* <p>Title: checkClassCanOpen</p> 
	* <p>Description: </p> 
	* @param classId
	* @return 
	* @see com.uws.training.dao.IStartupClassDao#checkClassCanOpen(java.lang.String) 
	*/
	@Override
	public boolean checkClassCanOpen(String classId) {
		String hql = " from StartupClassApplyInfo a where a.growthId.id = ? and "
				+ " a.applyStatus.code = 'PASS'";
		@SuppressWarnings("unchecked")
		List<StartupClassApplyInfo> applyList = this.query(hql, new Object[]{classId});
		if(applyList.size() > 0) {
			return true;
		}else{
			return false;
		}
	}

	@Override
	public List<StartupClassApplyInfo> getStuApplyInfoByClassId(
			String startupClassId) {
		String hql = "from StartupClassApplyInfo t where t.growthId.id =? ";
		return this.query(hql, new Object[]{startupClassId});
	}

	@Override
	public List<StartupClassApplyInfo> getStuApplyClassListByStuId(
			String studentId) {
		String hql = "from StartupClassApplyInfo t where t.studentId.id =? ";
		return this.query(hql, new Object[]{studentId});
	}

	@Override
	public List<StartupClassInfo> getStartupClassByLeaderId(String leaderId) {
		String hql =  "from StartupClassInfo t where t.growthClassLeader.id =?";
		return this.query(hql, new Object[]{leaderId});
	}

	@Override
	public StartupClassApplyInfo getStartupClassApplyInfoBystuIdAndClassId(
			String stuId, String classInfoId) {
		String hql = "from StartupClassApplyInfo t where t.growthId.id =? and t.studentId.id =? ";
		return (StartupClassApplyInfo) this.queryUnique(hql, new Object[]{classInfoId, stuId});
	}

	/* (非 Javadoc) 
	* <p>Title: getStuApplyListByStuAndClass</p> 
	* <p>Description: </p> 
	* @param studentId
	* @param classInfoId
	* @return 
	* @see com.uws.training.dao.IStartupClassDao#getStuApplyListByStuAndClass(java.lang.String, java.lang.String) 
	*/
	@SuppressWarnings("unchecked")
	@Override
	public List<StartupClassApplyInfo> getStuApplyListByStuAndClass(
			String studentId, String classInfoId) {
		String hql = " from StartupClassApplyInfo s where s.studentId.id = ? and s.growthId.id = ?";
		return this.query(hql, new Object[]{studentId,classInfoId});
	}

	@Override
	public Page queryMyStartClassInfoPage(StartupClassApplyInfo po, int pageNo, int pageSize) {
		List<String> values = new ArrayList<String>();
		StringBuffer hql = new StringBuffer(" from StartupClassApplyInfo t where 1 = 1 ");
		//创业班名称
		if(DataUtil.isNotNull(po.getGrowthId()) && DataUtil.isNotNull(po.getGrowthId().getGrowthClassName())) {
			hql.append("and t.growthId.growthClassName like ? ");
			values.add("%"+HqlEscapeUtil.escape(po.getGrowthId().getGrowthClassName())+"%");
		}
		//创业班类型
		if(DataUtil.isNotNull(po.getGrowthId()) && DataUtil.isNotNull(po.getGrowthId().getGrowthClassType()) && DataUtil.isNotNull(po.getGrowthId().getGrowthClassType().getId())) {
			hql.append("and t.growthId.growthClassType.id = ? ");
			values.add(po.getGrowthId().getGrowthClassType().getId());
		}
		//结业状态
		if(DataUtil.isNotNull(po.getCompleteStatus()) && DataUtil.isNotNull(po.getCompleteStatus().getId())) {
			hql.append("and t.completeStatus.id = ? ");
			values.add(po.getCompleteStatus().getId());
		}
		//按学生过滤
		if(DataUtil.isNotNull(po.getStudentId()) && DataUtil.isNotNull(po.getStudentId().getId())) {
			hql.append("and t.studentId.id = ? ");
			values.add(po.getStudentId().getId());
		}
		//按已开班并且是审核通过进行数据过滤
		hql.append("and (t.growthId.growthClassStatus.id = ?  or t.growthId.growthClassStatus.id = ?) ");
		values.add(dicUtil.getDicInfo("STARTUP_CLASS_STATUS", "OPENING").getId());
		values.add(dicUtil.getDicInfo("STARTUP_CLASS_STATUS", "CLOSED").getId());
		hql.append("and t.approveStatus.id = ? ");
		values.add(dicUtil.getDicInfo("STARTUP_APPROVE_STATUS", "APPROVED").getId());
		return this.pagedQuery(hql.toString(), pageNo, pageSize, values.toArray());
	}

}
