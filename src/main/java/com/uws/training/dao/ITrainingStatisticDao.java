package com.uws.training.dao;

import com.uws.core.hibernate.dao.IBaseDao;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.orientation.StudentInfoModel;
import com.uws.domain.training.AdvisorInfo;
import com.uws.domain.training.StatisticSeminarApply;
import com.uws.domain.training.StatisticStartupComplete;

public interface ITrainingStatisticDao extends IBaseDao {
	
	/**
	 * 查询视图用于讲座报名统计列表
	 * @param po
	 * @param mo
	 * @param co
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	Page querySeminarApplyStatisticPage(StatisticSeminarApply po, int pageNo,
			int pageSize);

	/**
	 * 查询用于创业班报名统计列表
	 * @param po
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	Page queryStartupClassApplyStatisticPage(StatisticStartupComplete po,
			int pageNo, int pageSize);

	/**
	 * 通过seminarId、growthId、advisorId
	 * 查询获得学生的信息
	 * @param po
	 * @param seminarId
	 * @param growthId
	 * @param flag
	 * @param advisorId
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	Page queryStuInfoPage(StudentInfoModel po, String seminarId,
			String growthId, String flag, String advisorId, int pageNo,
			int pageSize);

	/**
	 * 查询导师信息表
	 * @param po
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	Page queryAdvisorApplyStatisticPage(AdvisorInfo po, int pageNo, int pageSize);
}