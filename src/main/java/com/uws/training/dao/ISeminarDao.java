package com.uws.training.dao;

import java.util.List;

import com.uws.core.hibernate.dao.IBaseDao;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.base.BaseAcademyModel;
import com.uws.domain.training.OuterUserInfo;
import com.uws.domain.training.SeminarApply;
import com.uws.domain.training.SeminarManage;
import com.uws.domain.training.SeminarSubscribe;
import com.uws.domain.training.SeminarSubscribeList;

public interface ISeminarDao extends IBaseDao {

	/**
	 * 查询讲座信息返回页面
	 * @param po
	 * @param pageNo
	 * @return page
	 */
	Page queryPageSeminarInfo(SeminarManage po, int pageNo);

	/**
	 * 通过讲座id获得学院预约信息
	 * @param seminarInfoId
	 * @return
	 */
	List<SeminarSubscribe> getSeminarSubBySeminarInfoId(String seminarInfoId);

	/**
	 * 查询讲座信息返回列表，用于学院预约列表
	 * @param po
	 * @param vo
	 * @param pageNo
	 * @return
	 */
	Page queryPageSeminarSubscribeInfo(SeminarSubscribeList po, int pageNo);

	/**
	 * 通过当前登陆人的id获取所在学院
	 * @param userId
	 * @return
	 */
	BaseAcademyModel getBaseAcademyByTeacherId(String userId);

	/**
	 * 通过id查询学院预约信息表
	 * @param id
	 * @return
	 */
	SeminarSubscribe getSeminarSubscribeById(String id);

	/**
	 * 查询讲座信息返回列表，用于学生预约列表
	 * @param po
	 * @param pageNo
	 * @return
	 */
	Page queryPageSeminarApplyInfo(SeminarApply po, int pageNo);

	/**
	 * 通过学生id和讲座id获取该学生的预约信息
	 * @param seminarId
	 * @param studentId
	 * @return
	 */
	SeminarApply getSeminarApplyInfoBySeminarIdAndStudentId(String seminarId,
			String studentId);

	/**
	 * 通过学院id和讲座id获取该学院的预约信息
	 * @param seminarId
	 * @param collegeId
	 * @return
	 */
	SeminarSubscribe getSeminarSubscribeBySeminarIdAndCollegeId(
			String seminarId, String collegeId);

	/**
	 * 通过讲座的id获取学院预约信息
	 * @param seminarId
	 * @return
	 */
	Page queryPageSeminar(SeminarSubscribe po, int pageNo);

	/**
	 * 通过讲师类型id获取讲师列表
	 * @param teacherId
	 * @return
	 */
	List<OuterUserInfo> getOuterUserInfoListByAdvisorType(String advisorTypeId);
}