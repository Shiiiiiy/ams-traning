package com.uws.training.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.uws.core.hibernate.dao.impl.BaseDaoImpl;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.util.DataUtil;
import com.uws.core.util.HqlEscapeUtil;
import com.uws.domain.orientation.StudentInfoModel;
import com.uws.domain.training.AdvisorInfo;
import com.uws.domain.training.StatisticSeminarApply;
import com.uws.domain.training.StatisticStartupComplete;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.impl.DicFactory;
import com.uws.training.dao.ITrainingStatisticDao;

@Repository("com.uws.training.dao.impl.TrainingStatisticDaoImpl")
public class TrainingStatisticDaoImpl extends BaseDaoImpl implements
		ITrainingStatisticDao {
	@Autowired
	private static DicUtil dicUtil = DicFactory.getDicUtil();
	@Override
	public Page querySeminarApplyStatisticPage(StatisticSeminarApply po, int pageNo, int pageSize) {
		String hql = "from StatisticSeminarApply t where 1=1 ";
		Map<String, Object> params = new HashMap<String, Object>();
		//按讲座名称查询
		if(DataUtil.isNotNull(po) && DataUtil.isNotNull(po.getSeminarName())) {
			hql += "and t.seminarName like :seminarName ";
			params.put("seminarName", "%"+HqlEscapeUtil.escape(po.getSeminarName())+"%");
		}
		//按讲座主题查询
		if(DataUtil.isNotNull(po) && DataUtil.isNotNull(po.getSeminarTheme())) {
			hql += "and t.seminarTheme like :seminarTheme ";
			params.put("seminarTheme", "%"+HqlEscapeUtil.escape(po.getSeminarTheme())+"%");
		}
		//按主讲人名字查询
		if(DataUtil.isNotNull(po) && DataUtil.isNotNull(po.getOuterUserId()) && DataUtil.isNotNull(po.getOuterUserId().getUserName())) {
			hql += "and t.outerUserId.userName like :userName ";
			params.put("userName", "%"+HqlEscapeUtil.escape(po.getOuterUserId().getUserName())+"%");
		}
		//按讲座时间查询
		if(DataUtil.isNotNull(po) && DataUtil.isNotNull(po.getSeminarDateStr())) {
			String beginDate = po.getSeminarDateStr()+" 00:00:00";
			String endDate = po.getSeminarDateStr()+" 23:59:59";
			hql += "and (t.seminarDate between to_date('"+beginDate+"','yyyy-MM-dd hh24:MI:ss') and to_date('"+endDate+"','yyyy-MM-dd hh24:MI:ss')) ";
		}
		//按讲座地点查询
		if(DataUtil.isNotNull(po) && DataUtil.isNotNull(po.getAppointPlace())) {
			hql += "and t.appointPlace like :appointPlace ";
			params.put("appointPlace", "%"+HqlEscapeUtil.escape(po.getAppointPlace())+"%");
		}
		return this.pagedQuery(hql, params, pageSize, pageNo);
	}

	@Override
	public Page queryStartupClassApplyStatisticPage(StatisticStartupComplete po,
			int pageNo, int pageSize) {
		String hql = "from StatisticStartupComplete t where 1=1 ";
		Map<String, Object> params = new HashMap<String, Object>();
		//学年
		if(DataUtil.isNotNull(po) && DataUtil.isNotNull(po.getSchoolYear()) && DataUtil.isNotNull(po.getSchoolYear().getId())) {
			hql += "and t.schoolYear.id =:schoolYearId ";
			params.put("schoolYearId", po.getSchoolYear().getId());
		}
		//创业班名称
		if(DataUtil.isNotNull(po) && DataUtil.isNotNull(po.getClassName())) {
			hql += "and t.className like :className ";
			params.put("className", "%"+HqlEscapeUtil.escape(po.getClassName())+"%");
		}
		//创业班主题
		if(DataUtil.isNotNull(po) && DataUtil.isNotNull(po.getClassTheme())) {
			hql += "and t.classTheme like :classTheme ";
			params.put("classTheme", "%"+HqlEscapeUtil.escape(po.getClassTheme())+"%");
		}
		//创业班类型
		if(DataUtil.isNotNull(po) && DataUtil.isNotNull(po.getClassType()) && DataUtil.isNotNull(po.getClassType().getId())) {
			hql += "and t.classType.id = :classTypeId ";
			params.put("classTypeId", po.getClassType().getId());
		}
		//班主任
		if(DataUtil.isNotNull(po) && DataUtil.isNotNull(po.getTeacherName())) {
			hql += "and t.teacherName like :teacherName ";
			params.put("teacherName", "%"+HqlEscapeUtil.escape(po.getTeacherName())+"%");
		}
		//应为已开班的创业班
		hql += "and t.classStatus.id =:classStatusId ";
		params.put("classStatusId", dicUtil.getDicInfo("STARTUP_CLASS_STATUS", "CLOSED").getId());
		return this.pagedQuery(hql, params, pageSize, pageNo);
	}

	@Override
	public Page queryStuInfoPage(StudentInfoModel po, String seminarId,
			String growthId, String flag, String advisorId, int pageNo,
			int pageSize) {
//		String applyPass = dicUtil.getDicInfo("STARTUP_APPLY_STATUS", "APPLYED").getId();
		String complete = dicUtil.getDicInfo("STARTUP_COMPLETE_STATUS", "COMPLETED").getId();
		String uncomplete = dicUtil.getDicInfo("STARTUP_COMPLETE_STATUS", "UNCOMPLETE").getId();
		String hql = new String();
		Map<String, Object> params = new HashMap<String, Object>();
		if(DataUtil.isNotNull(seminarId)) {
			hql = "from SeminarApply t where t.seminarId.id =:seminarId ";
			params.put("seminarId", seminarId);
		}else if (DataUtil.isNotNull(growthId)) {
			hql = "from StartupClassApplyInfo t where t.growthId.id =:growthId and t.completeStatus.id =:completeStatusId ";
			params.put("growthId", growthId);
			if(flag.equals("complete")) {
				params.put("completeStatusId", complete);
			}else {
				params.put("completeStatusId", uncomplete);
			}
		}else if (DataUtil.isNotNull(advisorId)) {
			hql = "from AdvisorApplyInfo t where t.advisorInfoId.id =:advisorId ";
			params.put("advisorId", advisorId);
		}
		if(DataUtil.isNotNull(po) && DataUtil.isNotNull(po.getCollege()) && DataUtil.isNotNull(po.getCollege().getId())) {
			hql += "and t.studentId.college.id =:collegeId ";
			params.put("collegeId", po.getCollege().getId());
		}
		
		if(DataUtil.isNotNull(po) && DataUtil.isNotNull(po.getMajor()) && DataUtil.isNotNull(po.getMajor().getId())) {
			hql += "and t.studentId.major.id =:majorId ";
			params.put("majorId", po.getMajor().getId());
		}
		
		if(DataUtil.isNotNull(po) && DataUtil.isNotNull(po.getClassId()) && DataUtil.isNotNull(po.getClassId().getId())) {
			hql += "and t.studentId.classId.id =:classId ";
			params.put("classId", po.getClassId().getId());
		}
		hql += "order by t.studentId.stuNumber asc ";
		return this.pagedQuery(hql, params, pageSize, pageNo);
	}

	@Override
	public Page queryAdvisorApplyStatisticPage(AdvisorInfo po, int pageNo,
			int pageSize) {
		String hql = "from AdvisorInfo t where 1=1 ";
		Map<String, Object> params = new HashMap<String, Object>();
		if(DataUtil.isNotNull(po)) {
			//学年
			if(DataUtil.isNotNull(po.getSchoolYear()) && DataUtil.isNotNull(po.getSchoolYear().getId())) {
				hql += "and t.schoolYear.id =:schoolYearId ";
				params.put("schoolYearId", po.getSchoolYear().getId());	
			}
			if(DataUtil.isNotNull(po.getOuterUserInfo())) {
				//主讲人姓名
				if(DataUtil.isNotNull(po.getOuterUserInfo().getUserName())) {
					hql += "and t.outerUserInfo.userName like :userName ";
					params.put("userName", "%"+HqlEscapeUtil.escape(po.getOuterUserInfo().getUserName())+"%");
				}
				//主讲人性别
				if(DataUtil.isNotNull(po.getOuterUserInfo().getGender()) && DataUtil.isNotNull(po.getOuterUserInfo().getGender().getId())) {
					hql += "and t.outerUserInfo.gender.id =:genderId ";
					params.put("genderId", po.getOuterUserInfo().getGender().getId());
				}
			}
			//值班日期
			if(DataUtil.isNotNull(po.getAdvisorDateStr())) {
				String beginDate = po.getAdvisorDateStr()+" 00:00:00";
				String endDate = po.getAdvisorDateStr()+" 23:59:59";
				hql += "and (t.advisorDate between to_date('"+beginDate+"','yyyy-MM-dd hh24:MI:ss') and to_date('"+endDate+"','yyyy-MM-dd hh24:MI:ss')) ";
			}
		}
		return this.pagedQuery(hql, params, pageSize, pageNo);
	}

}