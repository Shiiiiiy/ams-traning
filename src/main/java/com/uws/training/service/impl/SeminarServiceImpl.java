package com.uws.training.service.impl;

import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uws.core.base.BaseServiceImpl;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.util.DataUtil;
import com.uws.domain.base.BaseAcademyModel;
import com.uws.domain.orientation.StudentInfoModel;
import com.uws.domain.training.OuterUserInfo;
import com.uws.domain.training.SeminarApply;
import com.uws.domain.training.SeminarManage;
import com.uws.domain.training.SeminarSubscribe;
import com.uws.domain.training.SeminarSubscribeList;
import com.uws.sys.model.UploadFileRef;
import com.uws.sys.service.FileUtil;
import com.uws.sys.service.impl.FileFactory;
import com.uws.training.dao.ISeminarDao;
import com.uws.training.service.ISeminarService;
import com.uws.user.model.Role;

@Service("com.uws.training.service.impl.SeminarServiceImpl")
public class SeminarServiceImpl extends BaseServiceImpl implements ISeminarService {
	private FileUtil fileUtil = FileFactory.getFileUtil();
	@Autowired
	private ISeminarDao seminarDao;
	
	@Override
	public Page queryPageSeminarInfo(SeminarManage po, int pageNo) {
		return this.seminarDao.queryPageSeminarInfo(po, pageNo);
	}
	
	@Override
	public SeminarManage getSeminarInfoById(String id) {
		return (SeminarManage) this.seminarDao.get(SeminarManage.class, id);
	}

	@Override
	public void updateSeminarInfo(SeminarManage po, String[] fileId) {
		this.seminarDao.update(po);
		//上传附件
		if(fileId != null) {
			if(ArrayUtils.isEmpty(fileId))
				fileId = new String[0];
			List<UploadFileRef> list = this.fileUtil.getFileRefsByObjectId(po.getId());
			for(UploadFileRef ufr : list) {
				if(!ArrayUtils.contains(fileId, ufr.getUploadFile().getId())) {
					this.fileUtil.deleteFormalFile(ufr);
				}
			}
			for(String id : fileId) {
				this.fileUtil.updateFormalFileTempTag(id, po.getId());
			}
		}
	}

	@Override
	public void saveSeminarInfo(SeminarManage po, String[] fileId) {
		this.seminarDao.save(po);
		//上传的附件进行处理
		if(ArrayUtils.isEmpty(fileId)) {
			return;
		}
		for(String id : fileId)
			this.fileUtil.updateFormalFileTempTag(id, po.getId());
	}

	@Override
	public void delSeminarInfo(String id) {
		this.seminarDao.deleteById(SeminarManage.class, id);
		
	}

	@Override
	public void saveSeminarSubInfo(SeminarSubscribe sub) {
		this.seminarDao.save(sub);
	}

	@Override
	public List<SeminarSubscribe> getSeminarSubBySeminarInfoId(String seminarInfoId) {
		return this.seminarDao.getSeminarSubBySeminarInfoId(seminarInfoId);
	}

	@Override
	public void delSeminarSub(String id) {
		this.seminarDao.deleteById(SeminarSubscribe.class, id);
		
	}

	@Override
	public Page queryPageSeminarSubscribeInfo(SeminarSubscribeList po, int pageNo) {
		return this.seminarDao.queryPageSeminarSubscribeInfo(po, pageNo);
	}

	@Override
	public BaseAcademyModel getBaseAcademyByTeacherId(String userId) {
		return this.seminarDao.getBaseAcademyByTeacherId(userId);
	}

	@Override
	public SeminarSubscribe getSeminarSubscribeById(String id) {
		return this.seminarDao.getSeminarSubscribeById(id);
	}

	@Override
	public Page queryPageSeminarApplyInfo(SeminarApply po, int pageNo) {
		return this.seminarDao.queryPageSeminarApplyInfo(po, pageNo);
	}

	@Override
	public void saveSeminarApplyInfo(SeminarApply sa) {
		this.seminarDao.save(sa);
	}

	@Override
	public SeminarApply getSeminarApplyInfoBySeminarIdAndStudentId(String seminarId,
			String studentId) {
		return this.seminarDao.getSeminarApplyInfoBySeminarIdAndStudentId(seminarId, studentId);
	}

	@Override
	public SeminarSubscribe getSeminarSubscribeBySeminarIdAndCollegeId(
			String seminarId, String collegeId) {
		return this.seminarDao.getSeminarSubscribeBySeminarIdAndCollegeId(seminarId, collegeId);
	}

	@Override
	public Page queryPageSeminar(SeminarSubscribe po, int pageNo) {
		return this.seminarDao.queryPageSeminar(po, pageNo);
		
	}

	@Override
	public List<OuterUserInfo> getOuterUserInfoListByAdvisorType(
			String advisorTypeId) {
		return this.seminarDao.getOuterUserInfoListByAdvisorType(advisorTypeId);
	}
}