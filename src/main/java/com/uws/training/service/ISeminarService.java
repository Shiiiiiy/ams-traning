package com.uws.training.service;

import java.util.List;

import com.uws.core.base.IBaseService;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.base.BaseAcademyModel;
import com.uws.domain.training.OuterUserInfo;
import com.uws.domain.training.SeminarApply;
import com.uws.domain.training.SeminarManage;
import com.uws.domain.training.SeminarSubscribe;
import com.uws.domain.training.SeminarSubscribeList;

public interface ISeminarService extends IBaseService {

	/**
	 * 查询返回页面
	 * @param po
	 * @param pageNo
	 * @return page
	 */
	Page queryPageSeminarInfo(SeminarManage po, int pageNo);

	/**
	 * 根据id查询数据库表
	 * @param id
	 * @return OuterUserInfo
	 */
	SeminarManage getSeminarInfoById(String id);

	/**
	 * 更新讲座信息
	 * @param po
	 */
	void updateSeminarInfo(SeminarManage po, String[] fileId);

	/**
	 * 保存讲座信息
	 * @param po
	 */
	void saveSeminarInfo(SeminarManage po, String[] fileId);

	/**
	 * 删除讲座信息
	 * @param id
	 */
	void delSeminarInfo(String id);

	/**
	 * 保存预约信息
	 * @param sub
	 */
	void saveSeminarSubInfo(SeminarSubscribe sub);

	/**
	 * 通过讲座id获取学院预约信息
	 * @param id
	 * @return
	 */
	List<SeminarSubscribe> getSeminarSubBySeminarInfoId(String id);

	/**
	 * 删除学院预约信息
	 * @param id
	 */
	void delSeminarSub(String id);

	/**
	 * 查询返回页面，用于学院预约列表
	 * @param po
	 * @param vo
	 * @param parseInt
	 * @return
	 */
	Page queryPageSeminarSubscribeInfo(SeminarSubscribeList po, int pageNo);

	/**
	 * 通过教师id获取当前教师所在的学院
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
	 * 查询返回页面，用于学生预约列表
	 * @param po
	 * @param parseInt
	 * @return
	 */
	Page queryPageSeminarApplyInfo(SeminarApply po, int pageNo);

	/**
	 * 保存学生报名讲座的信息
	 * @param sa
	 */
	void saveSeminarApplyInfo(SeminarApply sa);

	/**
	 * 通过学生id和讲座id获得学生报名信息
	 * @param id
	 * @param studentId
	 * @return
	 */
	SeminarApply getSeminarApplyInfoBySeminarIdAndStudentId(String seminarId,
			String studentId);

	/**
	 * 通过学院id和讲座id获取该学院预约该讲座的信息
	 * @param id
	 * @param id2
	 * @return
	 */
	SeminarSubscribe getSeminarSubscribeBySeminarIdAndCollegeId(String seminarId,
			String collegeId);

	/**
	 * 通过讲座的id获取学院的预约信息
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