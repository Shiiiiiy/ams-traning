package com.uws.training.service.impl;

import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uws.core.base.BaseServiceImpl;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.training.OuterUserInfo;
import com.uws.sys.model.UploadFileRef;
import com.uws.sys.service.FileUtil;
import com.uws.sys.service.impl.FileFactory;
import com.uws.training.dao.IOuterUserInfoDao;
import com.uws.training.service.IOuterUserInfoService;

@Service("com.uws.training.service.impl.OuterUserInfoServiceImpl")
public class OuterUserInfoServiceImpl extends BaseServiceImpl implements IOuterUserInfoService {
	private FileUtil fileUtil = FileFactory.getFileUtil();
	@Autowired
	private IOuterUserInfoDao outerUserInfoDao; 
	
	@Override
	public Page queryPageOuterUserInfo(OuterUserInfo po, int pageNo) {
		return this.outerUserInfoDao.queryPageOuterUserInfo(po, pageNo);
	}


	@Override
	public OuterUserInfo getOuterUserInfoById(String id) {
		return (OuterUserInfo) this.outerUserInfoDao.get(OuterUserInfo.class, id);
	}


	@Override
	public void saveOuterUserInfo(OuterUserInfo po, String[] fileId) {
		this.outerUserInfoDao.save(po);
		//上传的附件进行处理
		if(ArrayUtils.isEmpty(fileId)) {
			return;
		}
		for(String id : fileId)
			this.fileUtil.updateFormalFileTempTag(id, po.getId());
	}


	@Override
	public void updateOuterUserInfo(OuterUserInfo po, String[] fileId) {
		this.outerUserInfoDao.update(po);
		//上传头像
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
	public void delOuterUserInfo(String id) {
		this.outerUserInfoDao.deleteById(OuterUserInfo.class, id);
	}

	@Override
	public Long outerUserUsedCount(String userId) {
		return this.outerUserInfoDao.outerUserUsedCount(userId);
	}


	/* (非 Javadoc) 
	* <p>Title: getAllOuterUserList</p> 
	* <p>Description: </p> 
	* @return 
	* @see com.uws.training.service.IOuterUserInfoService#getAllOuterUserList() 
	*/
	@Override
	public List<OuterUserInfo> getAllOuterUserList() {
		return this.outerUserInfoDao.getAllOuterUserList();
	}
	
}