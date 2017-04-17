package com.uws.training.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uws.core.base.BaseServiceImpl;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.orientation.StudentInfoModel;
import com.uws.domain.training.AdvisorInfo;
import com.uws.domain.training.SeminarApply;
import com.uws.domain.training.StartupClassApplyInfo;
import com.uws.domain.training.StatisticSeminarApply;
import com.uws.domain.training.StatisticStartupComplete;
import com.uws.training.dao.ITrainingStatisticDao;
import com.uws.training.service.ITrainingStatisticService;

@Service("com.uws.training.service.impl.TrainingStatisticServiceImpl")
public class TrainingStatisticServiceImpl extends BaseServiceImpl implements
		ITrainingStatisticService {

	@Autowired
	private ITrainingStatisticDao trainingStatisticDao;

	@Override
	public Page querySeminarApplyStatisticPage(StatisticSeminarApply po,
			int pageNo, int pageSize) {
		return this.trainingStatisticDao.querySeminarApplyStatisticPage(po, pageNo, pageSize);
	}

	@Override
	public Page queryStartupClassApplyStatisticPage(StatisticStartupComplete po,
			int pageNo, int pageSize) {
		return this.trainingStatisticDao.queryStartupClassApplyStatisticPage(po, pageNo, pageSize);
	}

	@Override
	public Page queryStuInfoPage(StudentInfoModel po, String seminarId,
			String growthId, String flag, String advisorId, int pageNo,
			int pageSize) {
		return this.trainingStatisticDao.queryStuInfoPage(po, seminarId, growthId, flag, advisorId, pageNo, pageSize);
	}

	@Override
	public Page queryAdvisorApplyStatisticPage(AdvisorInfo po, int pageNo,
			int pageSize) {
		return this.trainingStatisticDao.queryAdvisorApplyStatisticPage(po, pageNo, pageSize);
	}
}