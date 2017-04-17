/**   
* @Title: AdvisorApplyDaoImpl.java 
* @Package com.uws.training.dao.impl 
* @Description: TODO(用一句话描述该文件做什么) 
* @author zhangyb   
* @date 2015年10月21日 下午1:27:38 
* @version V1.0   
*/
package com.uws.training.dao.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.uws.core.hibernate.dao.impl.BaseDaoImpl;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.util.DataUtil;
import com.uws.core.util.HqlEscapeUtil;
import com.uws.domain.training.AdvisorApplyInfo;
import com.uws.domain.training.AdvisorInfo;
import com.uws.sys.model.Dic;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.impl.DicFactory;
import com.uws.training.dao.IAdvisorApplyDao;

/** 
 * @ClassName: AdvisorApplyDaoImpl 
 * @Description:  导师预约管理dao
 * @author zhangyb 
 * @date 2015年10月21日 下午1:27:38  
 */
@Repository("advisorApplyDao")
public class AdvisorApplyDaoImpl extends BaseDaoImpl implements
		IAdvisorApplyDao {
	
	@Autowired
	private DicUtil dicUtil = DicFactory.getDicUtil();

	/* (非 Javadoc) 
	* <p>Title: queryAdvisorInfoPage</p> 
	* <p>Description: </p> 
	* @param advisorInfo
	* @param pageNo
	* @param pageSize
	* @return 
	* @see com.uws.training.dao.IAdvisorApplyDao#queryAdvisorInfoPage(com.uws.domain.training.AdvisorInfo, int, int) 
	*/
	@Override
	public Page queryAdvisorInfoPage(AdvisorInfo advisorInfo, int pageNo,
			int pageSize) {
		List<String> values = new ArrayList<String>();
		StringBuffer hql = new StringBuffer("from AdvisorInfo a where 1=1");
//		学年
		if(advisorInfo.getSchoolYear() != null 
				&& DataUtil.isNotNull(advisorInfo.getSchoolYear().getCode())) {
			hql.append(" and a.schoolYear.code = ?");
			values.add(advisorInfo.getSchoolYear().getCode());
		}
//		导师名称
		if(advisorInfo.getOuterUserInfo() != null 
				&& DataUtil.isNotNull(advisorInfo.getOuterUserInfo().getUserName())) {
			hql.append(" and a.outerUserInfo.userName like ?");
			values.add("%" + HqlEscapeUtil.escape(advisorInfo.getOuterUserInfo().getUserName()) + "%");
		}
//		状态
		if(advisorInfo.getAdvisorStatus() != null 
				&& DataUtil.isNotNull(advisorInfo.getAdvisorStatus().getCode())) {
			hql.append(" and a.advisorStatus.code = ?");
			values.add(advisorInfo.getAdvisorStatus().getCode());
		}
		hql.append(" order by updateTime desc");
		return this.pagedQuery(hql.toString(),pageNo,pageSize,values.toArray());
	}

	/* (非 Javadoc) 
	* <p>Title: saveAdvisorInfo</p> 
	* <p>Description: </p> 
	* @param advisorInfo 
	* @see com.uws.training.dao.IAdvisorApplyDao#saveAdvisorInfo(com.uws.domain.training.AdvisorInfo) 
	*/
	@Override
	public void saveAdvisorInfo(AdvisorInfo advisorInfo) {
		this.save(advisorInfo);
	}

	/* (非 Javadoc) 
	* <p>Title: updateAdvisorInfo</p> 
	* <p>Description: </p> 
	* @param advisorInfo 
	* @see com.uws.training.dao.IAdvisorApplyDao#updateAdvisorInfo(com.uws.domain.training.AdvisorInfo) 
	*/
	@Override
	public void updateAdvisorInfo(AdvisorInfo advisorInfo) {
		this.update(advisorInfo);
	}

	/* (非 Javadoc) 
	* <p>Title: deleteAdvisorInfo</p> 
	* <p>Description: </p> 
	* @param advisorInfo 
	* @see com.uws.training.dao.IAdvisorApplyDao#deleteAdvisorInfo(com.uws.domain.training.AdvisorInfo) 
	*/
	@Override
	public void deleteAdvisorInfo(AdvisorInfo advisorInfo) {
		this.delete(advisorInfo);
	}

	/* (非 Javadoc) 
	* <p>Title: getAdvisorById</p> 
	* <p>Description: </p> 
	* @param id
	* @return 
	* @see com.uws.training.dao.IAdvisorApplyDao#getAdvisorById(java.lang.String) 
	*/
	@Override
	public AdvisorInfo getAdvisorById(String id) {
		String hql = " from AdvisorInfo a where a.id = ?";
		return (AdvisorInfo) this.queryUnique(hql, new Object[]{id});
	}

	/* (非 Javadoc) 
	* <p>Title: queryAdvisorApplyPage</p> 
	* <p>Description: </p> 
	* @param advisorApply
	* @param pageNo
	* @param pageSize
	* @return 
	* @see com.uws.training.dao.IAdvisorApplyDao#queryAdvisorApplyPage(com.uws.domain.training.AdvisorApplyInfo, int, int) 
	*/
	@Override
	public Page queryAdvisorApplyPage(AdvisorApplyInfo advisorApply,
			int pageNo, int pageSize) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		List<String> values = new ArrayList<String>();
		StringBuffer hql = new StringBuffer("select yearDic.Name as schoolYear, o.user_name,"
				+ "o.id as outer_user_id,o.user_code,i.advisor_date,i.advisor_time,statusDic.Name,i.id as advisorId,a.id as id"
				+ " from hky_training_advisor_info i left join  hky_training_advisor_apply a"
				+ " on (i.id = a.advisor_id and a.student_id = ?)"
				+ " left join dic yearDic on yearDic.Id = i.school_year "
				+ " left join hky_training_outeruser_info o on o.id = i.outer_user_id "
				+ " left join dic statusDic on statusDic.Id = a.apply_status"
				+ " where i.advisor_status = ? and i.advisor_date > to_date(?,'yyyy-MM-dd')");
		values.add(advisorApply.getStudentId().getId());
		values.add(advisorApply.getAdvisorInfoId().getAdvisorStatus().getId());
		values.add(sdf.format(date));
//		学年
		if(advisorApply.getAdvisorInfoId() != null 
				&& advisorApply.getAdvisorInfoId().getSchoolYear() != null 
				&& DataUtil.isNotNull(advisorApply.getAdvisorInfoId().getSchoolYear().getId())) {
			hql.append(" and i.school_year = ?");
			values.add(advisorApply.getAdvisorInfoId().getSchoolYear().getId());
		}
//		导师名称
		if(advisorApply.getAdvisorInfoId() != null 
				&& advisorApply.getAdvisorInfoId().getOuterUserInfo() != null 
				&& DataUtil.isNotNull(advisorApply.getAdvisorInfoId().getOuterUserInfo().getUserName())) {
			hql.append(" and o.user_name like ?");
			values.add("%" + HqlEscapeUtil.escape(advisorApply.getAdvisorInfoId().getOuterUserInfo().getUserName()) + "%");
		}
//		状态
		if(advisorApply.getApplyStatus() != null 
				&& DataUtil.isNotNull(advisorApply.getApplyStatus().getId())) {
			Dic applyDic = this.dicUtil.getDicInfo("STUDENT_ADVISOR_APPLY_STATUS", "UNAPPLY");
			if(applyDic.getId().equals(advisorApply.getApplyStatus().getId())) {
				hql.append(" and a.apply_status is null");
			}else{
				hql.append(" and a.apply_status = ?");
				values.add(advisorApply.getApplyStatus().getId());
			}
		}
		hql.append(" order by i.update_time desc");
		return this.pagedSQLQuery(hql.toString(), pageNo, pageSize, values.toArray());
	}

	/* (非 Javadoc) 
	* <p>Title: saveAdvisorApply</p> 
	* <p>Description: </p> 
	* @param advisorApplyInfo 
	* @see com.uws.training.dao.IAdvisorApplyDao#saveAdvisorApply(com.uws.domain.training.AdvisorApplyInfo) 
	*/
	@Override
	public void saveAdvisorApply(AdvisorApplyInfo advisorApplyInfo) {
		this.save(advisorApplyInfo);
	}

	/* (非 Javadoc) 
	* <p>Title: updateAdvisorApply</p> 
	* <p>Description: </p> 
	* @param advisorApplyInfo 
	* @see com.uws.training.dao.IAdvisorApplyDao#updateAdvisorApply(com.uws.domain.training.AdvisorApplyInfo) 
	*/
	@Override
	public void updateAdvisorApply(AdvisorApplyInfo advisorApplyInfo) {
		this.update(advisorApplyInfo);
	}

	/* (非 Javadoc) 
	* <p>Title: deleteAdvisorApply</p> 
	* <p>Description: </p> 
	* @param advisorApplyInfo 
	* @see com.uws.training.dao.IAdvisorApplyDao#deleteAdvisorApply(com.uws.domain.training.AdvisorApplyInfo) 
	*/
	@Override
	public void deleteAdvisorApply(AdvisorApplyInfo advisorApplyInfo) {
		this.delete(advisorApplyInfo);
	}

	/* (非 Javadoc) 
	* <p>Title: getAdvisorApplyById</p> 
	* <p>Description: </p> 
	* @param id
	* @return 
	* @see com.uws.training.dao.IAdvisorApplyDao#getAdvisorApplyById(java.lang.String) 
	*/
	@Override
	public AdvisorApplyInfo getAdvisorApplyById(String id) {
		String hql = " from AdvisorApplyInfo a where a.id = ?";
		return (AdvisorApplyInfo) this.queryUnique(hql, new Object[]{id});
	}

	/* (非 Javadoc) 
	* <p>Title: getAdvisorListByUserId</p> 
	* <p>Description: </p> 
	* @param userId
	* @param date
	* @return 
	* @see com.uws.training.dao.IAdvisorApplyDao#getAdvisorListByUserId(java.lang.String, java.lang.String) 
	*/
	@SuppressWarnings("unchecked")
	@Override
	public List<AdvisorInfo> getAdvisorListByUserId(String userId, Date date) {
		String hql = " from AdvisorInfo a where a.outerUserInfo.id = ? and a.advisorDate = ?";
		return this.query(hql, new Object[]{userId,date});
	}

}
