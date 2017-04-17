package com.uws.training.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.uws.core.hibernate.dao.impl.BaseDaoImpl;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.util.DataUtil;
import com.uws.core.util.HqlEscapeUtil;
import com.uws.domain.base.BaseAcademyModel;
import com.uws.domain.training.OuterUserInfo;
import com.uws.domain.training.SeminarApply;
import com.uws.domain.training.SeminarManage;
import com.uws.domain.training.SeminarSubscribe;
import com.uws.domain.training.SeminarSubscribeList;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.impl.DicFactory;
import com.uws.training.dao.ISeminarDao;

@Repository("com.uws.training.dao.impl.SeminarDaoImpl")
public class SeminarDaoImpl extends BaseDaoImpl implements ISeminarDao {
	@Autowired
	private static DicUtil dicUtil = DicFactory.getDicUtil();

	@Override
	public Page queryPageSeminarInfo(SeminarManage po, int pageNo) {
		String hql = "from SeminarManage where 1=1 ";
		Map<String, Object> params = new HashMap<String, Object>();
		//讲座名称
		if(DataUtil.isNotNull(po) && DataUtil.isNotNull(po.getSeminarName())) {
			hql += "and seminarName like :seminarName ";
			if(HqlEscapeUtil.IsNeedEscape(po.getSeminarName())) {
				params.put("seminarName", "%"+HqlEscapeUtil.escape(po.getSeminarName())+"%");
				hql += HqlEscapeUtil.HQL_ESCAPE;
			}else {
				params.put("seminarName", "%"+po.getSeminarName()+"%");
			}
		}
		//主讲人
		if(DataUtil.isNotNull(po) && DataUtil.isNotNull(po.getOuterUserId()) && DataUtil.isNotNull(po.getOuterUserId().getUserName())) {
			hql += "and outerUserId.userName like :userName ";
			if(HqlEscapeUtil.IsNeedEscape(po.getOuterUserId().getUserName())) {
				params.put("userName", "%"+HqlEscapeUtil.escape(po.getOuterUserId().getUserName())+"%");
				hql += HqlEscapeUtil.HQL_ESCAPE;
			}else {
				params.put("userName", "%"+po.getOuterUserId().getUserName()+"%");
			}
		}
		//讲座状态
		if(DataUtil.isNotNull(po) && DataUtil.isNotNull(po.getSeminarStatus()) && DataUtil.isNotNull(po.getSeminarStatus().getId())) {
			hql += "and seminarStatus.id =:seminarStatusId ";
			params.put("seminarStatusId", po.getSeminarStatus().getId());
		}
		hql += "order by seminarStatus.id asc ";
		return this.pagedQuery(hql, params, Page.DEFAULT_PAGE_SIZE, pageNo);
	}

	@Override
	public List<SeminarSubscribe> getSeminarSubBySeminarInfoId(
			String seminarInfoId) {
		String hql = "select t.id from SeminarSubscribe t where t.seminarId.id = ? ";
		return this.query(hql, new String[]{seminarInfoId});
	}

	@Override
	public Page queryPageSeminarSubscribeInfo(SeminarSubscribeList po, int pageNo) {
		String appoint = dicUtil.getDicInfo("COLLEGE_APPOINT_STATUS", "APPOINT").getId();
		String hql = "from SeminarSubscribeList t where 1=1 ";
		Map<String, Object> params = new HashMap<String, Object>();
		if(DataUtil.isNotNull(po) && DataUtil.isNotNull(po.getCollegeId()) && DataUtil.isNotNull(po.getCollegeId().getId())) {
			hql+="and t.collegeId.id =:collegeId ";
			params.put("collegeId", po.getCollegeId().getId());
		}
		//讲座名称查询
		if(DataUtil.isNotNull(po) && DataUtil.isNotNull(po.getSeminarId()) && DataUtil.isNotNull(po.getSeminarId().getSeminarName())) {
			hql +="and t.seminarId.seminarName like :seminarName ";
			if(HqlEscapeUtil.IsNeedEscape(po.getSeminarId().getSeminarName())) {
				params.put("semianrName", "%"+HqlEscapeUtil.escape(po.getSeminarId().getSeminarName())+"%");
				hql += HqlEscapeUtil.HQL_ESCAPE;
			}else {
				params.put("seminarName", "%"+po.getSeminarId().getSeminarName()+"%");
			}
		}
		//主讲人查询
		if(DataUtil.isNotNull(po) && DataUtil.isNotNull(po.getOuterUserId()) && DataUtil.isNotNull(po.getOuterUserId().getUserName())) {
			hql +="and t.outerUserId.userName like :outerUserName ";
			if(HqlEscapeUtil.IsNeedEscape(po.getOuterUserId().getUserName())) {
				params.put("outerUserName", "%"+HqlEscapeUtil.escape(po.getOuterUserId().getUserName())+"%");
				hql += HqlEscapeUtil.HQL_ESCAPE;
			}else {
				params.put("outerUserName", "%"+po.getOuterUserId().getUserName()+"%");
			}
		}
		//预约状态查询
		if(DataUtil.isNotNull(po) && DataUtil.isNotNull(po.getAppointStatus()) && DataUtil.isNotNull(po.getAppointStatus().getId())) {
			if(po.getAppointStatus().getId().equals(appoint)) {
				//未预约
				hql +="and t.appointStatus is null ";
			}else {
				//已预约
				hql +="and t.appointStatus.id =:appointStatus ";
				params.put("appointStatus", po.getAppointStatus().getId());
			}
		}
		hql += "order by t.appointStatus.id desc ";
		return this.pagedQuery(hql, params, Page.DEFAULT_PAGE_SIZE, pageNo);
	}

	@Override
	public BaseAcademyModel getBaseAcademyByTeacherId(String userId) {
		String hql = "from BaseAcademyModel t where t.id = ? ";
		return (BaseAcademyModel) this.queryUnique(hql, new String[]{userId});
	}

	@Override
	public SeminarSubscribe getSeminarSubscribeById(String id) {
		String hql = "from SeminarSubscribe t where t.id =? ";
		return (SeminarSubscribe) this.queryUnique(hql, new String[]{id});
	}

	@Override
	public Page queryPageSeminarApplyInfo(SeminarApply po, int pageNo) {
		//讲座状态为已发布
		String seminarStatusId = dicUtil.getDicInfo("SEMINAR_STATUS","PUBLISH").getId();
		String sql ="select t.seminar_name, t.appoint_place, t.outer_user_id, o.user_name, sa.sign_status, t.seminar_id, t.student_id, t.seminar_Date " +
				"from(select sm.id as seminar_id, sm.appoint_place as appoint_place, sm.seminar_name as seminar_name, sm.outer_user_id as outer_User_Id, sim.id as student_id, sm.seminar_Date as seminar_Date " +
				"from hky_training_seminar_info sm, hky_student_info sim where sim.id =? and sm.seminar_status =? ) t " +
				"left join hky_training_seminar_apply sa on (sa.seminar_id= t.seminar_id and sa.student_id = t.student_id) " +
				"left join hky_training_outeruser_info o on o.id = t.outer_user_id where 1=1 ";
		List<String> values = new ArrayList<String>();
		if(DataUtil.isNotNull(po) && DataUtil.isNotNull(po.getStudentId()) && DataUtil.isNotNull(po.getStudentId().getId())) {
			values.add(po.getStudentId().getId());
		}else {
			values.add(null);
		}
		values.add(seminarStatusId);
		//讲座名称查询
		if(DataUtil.isNotNull(po) && DataUtil.isNotNull(po.getSeminarId()) && DataUtil.isNotNull(po.getSeminarId().getSeminarName())) {
			sql += "and t.seminar_name like ? ";
			values.add("%" + HqlEscapeUtil.escape(po.getSeminarId().getSeminarName()) + "%");
		}
		//主讲人查询
		if(DataUtil.isNotNull(po) && DataUtil.isNotNull(po.getSeminarId()) && DataUtil.isNotNull(po.getSeminarId().getOuterUserId()) && 
				DataUtil.isNotNull(po.getSeminarId().getOuterUserId().getUserName())) {
			sql += "and o.user_name like ? ";
			values.add("%" + HqlEscapeUtil.escape(po.getSeminarId().getOuterUserId().getUserName()) + "%");
		}
		//报名状态查询
		if(DataUtil.isNotNull(po) && DataUtil.isNotNull(po.getApplyStatus()) && DataUtil.isNotNull(po.getApplyStatus().getId())) {
			sql += "and sa.sign_status =? ";
			values.add(po.getApplyStatus().getId());
		}
		sql +="order by sa.sign_status desc ";
		return this.pagedSQLQuery(sql, pageNo, Page.DEFAULT_PAGE_SIZE, values.toArray());
	}

	@Override
	public SeminarApply getSeminarApplyInfoBySeminarIdAndStudentId(
			String seminarId, String studentId) {
		String hql = "from SeminarApply t where t.seminarId.id =? and t.studentId.id =? ";
		return (SeminarApply) this.queryUnique(hql, new String[]{seminarId, studentId});
	}

	@Override
	public SeminarSubscribe getSeminarSubscribeBySeminarIdAndCollegeId(
			String seminarId, String collegeId) {
		String hql = "from SeminarSubscribe t where t.seminarId.id =? and t.appointAcademy.id =? ";
		return (SeminarSubscribe) this.queryUnique(hql, new String[]{seminarId, collegeId});
	}

	@Override
	public Page queryPageSeminar(SeminarSubscribe po, int pageNo) {
		String hql = "from SeminarSubscribe t where 1=1 ";
		Map<String, Object> params = new HashMap<String, Object>();
		if(DataUtil.isNotNull(po) && DataUtil.isNotNull(po.getId())) {
			hql +="and seminarId.id =:seminarId ";
			params.put("seminarId", po.getId());
		}
		return this.pagedQuery(hql, params, Page.DEFAULT_PAGE_SIZE, pageNo);
	}

	@Override
	public List<OuterUserInfo> getOuterUserInfoListByAdvisorType(
			String advisorTypeId) {
		String userStatusId = dicUtil.getDicInfo("STATUS_ENABLE_DISABLE", "ENABLE").getId();
		String hql = "from OuterUserInfo o where o.userStatus.id =? and o.userType.id =? ";
		return this.query(hql, new String[]{userStatusId, advisorTypeId});
	}

}
