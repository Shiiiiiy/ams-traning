
package com.uws.training.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uws.core.base.BaseServiceImpl;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.training.AdvisorApplyInfo;
import com.uws.domain.training.AdvisorInfo;
import com.uws.sys.model.UploadFileRef;
import com.uws.sys.service.FileUtil;
import com.uws.sys.service.impl.FileFactory;
import com.uws.training.dao.IAdvisorApplyDao;
import com.uws.training.service.IAdvisorApplyService;

/**   
* @Title: AdvisorApplyServiceImpl.java 
* @Package com.uws.training.service.impl 
* @Description: TODO(导师预约serviceImpl) 
* @author zhangyb   
* @date 2015年10月21日 下午1:21:31 
* @version V1.0   
*/
@Service("advisorApplyService")
public class AdvisorApplyServiceImpl extends BaseServiceImpl implements
		IAdvisorApplyService {
	
	@Autowired
	private IAdvisorApplyDao advisorApplyDao;
	private FileUtil fileUtil=FileFactory.getFileUtil();

	/* (非 Javadoc) 
	* <p>Title: queryAdvisorInfoPage</p> 
	* <p>Description: </p> 
	* @param advisorInfo
	* @param pageNo
	* @param pageSize
	* @return 
	* @see com.uws.training.service.IAdvisorApplyService#queryAdvisorInfoPage(com.uws.domain.training.AdvisorInfo, int, int) 
	*/
	@Override
	public Page queryAdvisorInfoPage(AdvisorInfo advisorInfo, int pageNo,
			int pageSize) {
		return this.advisorApplyDao.queryAdvisorInfoPage(advisorInfo, pageNo, pageSize);
	}

	/* (非 Javadoc) 
	* <p>Title: saveAdvisorInfo</p> 
	* <p>Description: </p> 
	* @param advisorInfo 
	* @see com.uws.training.service.IAdvisorApplyService#saveAdvisorInfo(com.uws.domain.training.AdvisorInfo) 
	*/
	@Override
	public void saveAdvisorInfo(AdvisorInfo advisorInfo,String[] fileId) {
		this.advisorApplyDao.saveAdvisorInfo(advisorInfo);
		if (ArrayUtils.isEmpty(fileId))
		       fileId = new String[0];
		     List<UploadFileRef> list = this.fileUtil.getFileRefsByObjectId(advisorInfo.getId());
		     for (UploadFileRef ufr : list) {
		       if (!ArrayUtils.contains(fileId, ufr.getUploadFile().getId()))
		         this.fileUtil.deleteFormalFile(ufr);
		    }
		     for (String id : fileId){
		       this.fileUtil.updateFormalFileTempTag(id, advisorInfo.getId());
		    }
	}

	/* (非 Javadoc) 
	* <p>Title: updateAdvisorInfo</p> 
	* <p>Description: </p> 
	* @param advisorInfo 
	* @see com.uws.training.service.IAdvisorApplyService#updateAdvisorInfo(com.uws.domain.training.AdvisorInfo) 
	*/
	@Override
	public void updateAdvisorInfo(AdvisorInfo advisorInfo,String[] fileId) {
		this.advisorApplyDao.updateAdvisorInfo(advisorInfo);
		if(fileId != null) {
			if (ArrayUtils.isEmpty(fileId)){
				fileId = new String[0];
			}
			List<UploadFileRef> list = this.fileUtil.getFileRefsByObjectId(advisorInfo.getId());
			for (UploadFileRef ufr : list) {
				if (!ArrayUtils.contains(fileId, ufr.getUploadFile().getId())){
					this.fileUtil.deleteFormalFile(ufr);
				}
			}
			for (String id : fileId){
				this.fileUtil.updateFormalFileTempTag(id, advisorInfo.getId());
			}
		}
	}
	
	@Override
	public void updateAdvisorInfo(AdvisorInfo advisorInfo) {
		this.advisorApplyDao.updateAdvisorInfo(advisorInfo);
	}

	/* (非 Javadoc) 
	* <p>Title: deleteAdvisorInfo</p> 
	* <p>Description: </p> 
	* @param advisorInfo 
	* @see com.uws.training.service.IAdvisorApplyService#deleteAdvisorInfo(com.uws.domain.training.AdvisorInfo) 
	*/
	@Override
	public void deleteAdvisorInfo(AdvisorInfo advisorInfo) {
		this.advisorApplyDao.deleteAdvisorInfo(advisorInfo);
	}

	/* (非 Javadoc) 
	* <p>Title: getAdvisorById</p> 
	* <p>Description: </p> 
	* @param id
	* @return 
	* @see com.uws.training.service.IAdvisorApplyService#getAdvisorById(java.lang.String) 
	*/
	@Override
	public AdvisorInfo getAdvisorById(String id) {
		return this.advisorApplyDao.getAdvisorById(id);
	}

	/* (非 Javadoc) 
	* <p>Title: queryAdvisorApplyPage</p> 
	* <p>Description: </p> 
	* @param advisorApply
	* @param pageNo
	* @param pageSize
	* @return 
	* @see com.uws.training.service.IAdvisorApplyService#queryAdvisorApplyPage(com.uws.domain.training.AdvisorApplyInfo, int, int) 
	*/
	@Override
	public Page queryAdvisorApplyPage(AdvisorApplyInfo advisorApply,
			int pageNo, int pageSize) {
		return this.advisorApplyDao.queryAdvisorApplyPage(advisorApply, pageNo, pageSize);
	}

	/* (非 Javadoc) 
	* <p>Title: saveAdvisorApply</p> 
	* <p>Description: </p> 
	* @param advisorApplyInfo 
	* @see com.uws.training.service.IAdvisorApplyService#saveAdvisorApply(com.uws.domain.training.AdvisorApplyInfo) 
	*/
	@Override
	public void saveAdvisorApply(AdvisorApplyInfo advisorApplyInfo) {
		this.advisorApplyDao.saveAdvisorApply(advisorApplyInfo);
	}

	/* (非 Javadoc) 
	* <p>Title: updateAdvisorApply</p> 
	* <p>Description: </p> 
	* @param advisorApplyInfo 
	* @see com.uws.training.service.IAdvisorApplyService#updateAdvisorApply(com.uws.domain.training.AdvisorApplyInfo) 
	*/
	@Override
	public void updateAdvisorApply(AdvisorApplyInfo advisorApplyInfo) {
		this.advisorApplyDao.updateAdvisorApply(advisorApplyInfo);
	}

	/* (非 Javadoc) 
	* <p>Title: deleteAdvisorApply</p> 
	* <p>Description: </p> 
	* @param advisorApplyInfo 
	* @see com.uws.training.service.IAdvisorApplyService#deleteAdvisorApply(com.uws.domain.training.AdvisorApplyInfo) 
	*/
	@Override
	public void deleteAdvisorApply(AdvisorApplyInfo advisorApplyInfo) {
		this.advisorApplyDao.deleteAdvisorApply(advisorApplyInfo);
	}

	/* (非 Javadoc) 
	* <p>Title: getAdvisorApplyById</p> 
	* <p>Description: </p> 
	* @param id
	* @return 
	* @see com.uws.training.service.IAdvisorApplyService#getAdvisorApplyById(java.lang.String) 
	*/
	@Override
	public AdvisorApplyInfo getAdvisorApplyById(String id) {
		return this.advisorApplyDao.getAdvisorApplyById(id);
	}

	/* (非 Javadoc) 
	* <p>Title: getAdvisorListByUserId</p> 
	* <p>Description: </p> 
	* @param userId
	* @param date
	* @return 
	* @see com.uws.training.service.IAdvisorApplyService#getAdvisorListByUserId(java.lang.String, java.lang.String) 
	*/
	@Override
	public List<AdvisorInfo> getAdvisorListByUserId(String userId, Date date) {
		// TODO Auto-generated method stub
		return this.advisorApplyDao.getAdvisorListByUserId(userId, date);
	}

}
