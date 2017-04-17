/**   
* @Title: StartupClassServiceImpl.java 
* @Package com.uws.training.service.impl 
* @Description: (用一句话描述该文件做什么) 
* @author zhangyb   
* @date 2015年10月21日 下午1:33:43 
* @version V1.0   
*/
package com.uws.training.service.impl;

import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uws.common.dao.ICommonRoleDao;
import com.uws.core.base.BaseServiceImpl;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.training.StartupClassApplyInfo;
import com.uws.domain.training.StartupClassInfo;
import com.uws.sys.model.Dic;
import com.uws.sys.model.UploadFileRef;
import com.uws.sys.service.FileUtil;
import com.uws.sys.service.impl.FileFactory;
import com.uws.training.dao.IStartupClassDao;
import com.uws.training.service.IStartupClassService;

/** 
 * @ClassName: StartupClassServiceImpl 
 * @Description:  创业班管理service
 * @author zhangyb 
 * @date 2015年10月21日 下午1:33:43  
 */
@Service("startupClassService")
public class StartupClassServiceImpl extends BaseServiceImpl implements
		IStartupClassService {

	@Autowired
	private IStartupClassDao startupClassDao;
	@Autowired
	private ICommonRoleDao commonRoleDao;
	private FileUtil fileUtil=FileFactory.getFileUtil();
	
	@Override
	public Page queryStartupClassPage(StartupClassInfo startupClassInfo,
			int pageNo, int pageSize, String flag) {
		return this.startupClassDao.queryStartupClassPage(startupClassInfo, pageNo, pageSize, flag);
	}

	@Override
	public void saveStartupClass(StartupClassInfo startupClassInfo,
			String[] fileId) {
		this.startupClassDao.saveStartupClass(startupClassInfo, fileId);  
		if (ArrayUtils.isEmpty(fileId))
		       fileId = new String[0];
		     List<UploadFileRef> list = this.fileUtil.getFileRefsByObjectId(startupClassInfo.getId());
		     for (UploadFileRef ufr : list) {
		       if (!ArrayUtils.contains(fileId, ufr.getUploadFile().getId()))
		         this.fileUtil.deleteFormalFile(ufr);
		    }
		     for (String id : fileId){
		       this.fileUtil.updateFormalFileTempTag(id, startupClassInfo.getId());
		    }
	}

	@Override
	public void updateStartupClass(StartupClassInfo startupClassInfo,
			String[] fileId) {
		//  Auto-generated method stub
		this.startupClassDao.updateStartupClass(startupClassInfo, fileId);
		if (ArrayUtils.isEmpty(fileId))
		       fileId = new String[0];
		     List<UploadFileRef> list = this.fileUtil.getFileRefsByObjectId(startupClassInfo.getId());
		     for (UploadFileRef ufr : list) {
		       if (!ArrayUtils.contains(fileId, ufr.getUploadFile().getId()))
		         this.fileUtil.deleteFormalFile(ufr);
		    }
		     for (String id : fileId){
		       this.fileUtil.updateFormalFileTempTag(id, startupClassInfo.getId());
		    }
	}

	@Override
	public void updateStartupClass(StartupClassInfo startupClassInfo) {
		this.startupClassDao.updateStartupClass(startupClassInfo, null);
	}
	
	@Override
	public void deleteStartupClass(StartupClassInfo startupClassInfo) {
		//  Auto-generated method stub
		List<UploadFileRef> list = this.fileUtil.getFileRefsByObjectId(startupClassInfo.getId());
		if(list.size() > 0) {
			for (UploadFileRef ufr : list) {
				this.fileUtil.deleteFormalFile(ufr);
			}
		}
		this.startupClassDao.deleteStartupClass(startupClassInfo);
	}

	/* (非 Javadoc) 
	* <p>Title: getStartupClassById</p> 
	* <p>Description: </p> 
	* @param classInfoId
	* @return 
	* @see com.uws.training.service.IStartupClassService#getStartupClassById(java.lang.String) 
	*/
	@Override
	public StartupClassInfo getStartupClassById(String classInfoId) {
		return this.startupClassDao.getClassInfoById(classInfoId);
	}

	/* (非 Javadoc) 
	* <p>Title: getClassInfoListByName</p> 
	* <p>Description: </p> 
	* @param className
	* @return 
	* @see com.uws.training.service.IStartupClassService#getClassInfoListByName(java.lang.String) 
	*/
	@Override
	public List<StartupClassInfo> getClassInfoListByName(String className) {
		return this.startupClassDao.getClassInfoListByName(className);
	}

	/* (非 Javadoc) 
	* <p>Title: queryStuApplyPage</p> 
	* <p>Description: </p> 
	* @param startupClassInfo
	* @param pageNo
	* @param pageSize
	* @param userId
	* @return 
	* @see com.uws.training.service.IStartupClassService#queryStuApplyPage(com.uws.domain.training.StartupClassInfo, int, int, java.lang.String) 
	*/
	@Override
	public Page queryStuApplyPage(StartupClassInfo startupClassInfo,
			int pageNo, int pageSize,String userId,Dic applyStatus,Dic approveStatus) {
		return this.startupClassDao.queryStuApplyPage(startupClassInfo, pageNo, pageSize,userId,applyStatus,approveStatus);
	}

	/* (非 Javadoc) 
	* <p>Title: saveStuApplyInfo</p> 
	* <p>Description: </p> 
	* @param applyInfo 
	* @see com.uws.training.service.IStartupClassService#saveStuApplyInfo(com.uws.domain.training.StartupClassApplyInfo) 
	*/
	@Override
	public void saveStuApplyInfo(StartupClassApplyInfo applyInfo) {
		this.startupClassDao.saveStuApplyInfo(applyInfo);
	}

	/* (非 Javadoc) 
	* <p>Title: updateStuApplyInfo</p> 
	* <p>Description: </p> 
	* @param applyInfo 
	* @see com.uws.training.service.IStartupClassService#updateStuApplyInfo(com.uws.domain.training.StartupClassApplyInfo) 
	*/
	@Override
	public void updateStuApplyInfo(StartupClassApplyInfo applyInfo) {
		this.startupClassDao.updateStuApplyInfo(applyInfo);
	}

	/* (非 Javadoc) 
	* <p>Title: delStuApplyInfo</p> 
	* <p>Description: </p> 
	* @param applyInfo 
	* @see com.uws.training.service.IStartupClassService#delStuApplyInfo(com.uws.domain.training.StartupClassApplyInfo) 
	*/
	@Override
	public void delStuApplyInfo(StartupClassApplyInfo applyInfo) {
		this.startupClassDao.delStuApplyInfo(applyInfo);
	}

	/* (非 Javadoc) 
	* <p>Title: getStuApplyInfoById</p> 
	* <p>Description: </p> 
	* @param id
	* @return 
	* @see com.uws.training.service.IStartupClassService#getStuApplyInfoById(java.lang.String) 
	*/
	@Override
	public StartupClassApplyInfo getStuApplyInfoById(String id) {
		return this.startupClassDao.getStuApplyInfoById(id);
	}

	/* (非 Javadoc) 
	* <p>Title: queryStuApprovePage</p> 
	* <p>Description: </p> 
	* @param applyInfo
	* @param pageNo
	* @param pageSize
	* @return 
	* @see com.uws.training.service.IStartupClassService#queryStuApprovePage(com.uws.domain.training.StartupClassApplyInfo, int, int) 
	*/
	@Override
	public Page queryStuApprovePage(StartupClassApplyInfo applyInfo,
			int pageNo, int pageSize, String userId) {
		return this.startupClassDao.queryStuApprovePage(applyInfo, pageNo, pageSize, userId);
	}

	/* (非 Javadoc) 
	* <p>Title: getSubClassInfoList</p> 
	* <p>Description: </p> 
	* @param classStatus
	* @return 
	* @see com.uws.training.service.IStartupClassService#getSubClassInfoList(com.uws.sys.model.Dic) 
	*/
	@Override
	public List<StartupClassInfo> getSubClassInfoList(Dic classStatus, String userId) {
		return this.startupClassDao.getSubClassInfoList(classStatus, userId);
	}

	/* (非 Javadoc) 
	* <p>Title: updateUserRole</p> 
	* <p>Description: </p> 
	* @param orginUserId
	* @param updateUserId
	* @param roleCode 
	* @see com.uws.training.service.IStartupClassService#updateUserRole(java.lang.String, java.lang.String, java.lang.String) 
	*/
	@Override
	public void updateUserRole(String orginUserId, String updateUserId,
			String roleCode) {
		//  Auto-generated method stub
		this.commonRoleDao.updateUserRole(orginUserId, updateUserId, roleCode);
	}

	/* (非 Javadoc) 
	* <p>Title: saveUserRole</p> 
	* <p>Description: </p> 
	* @param userId
	* @param roleCode 
	* @see com.uws.training.service.IStartupClassService#saveUserRole(java.lang.String, java.lang.String) 
	*/
	@Override
	public void saveUserRole(String userId, String roleCode) {
		//  Auto-generated method stub
		this.commonRoleDao.saveUserRole(userId, roleCode);
	}

	/* (非 Javadoc) 
	* <p>Title: deleteUserRole</p> 
	* <p>Description: </p> 
	* @param userId
	* @param roleCode 
	* @see com.uws.training.service.IStartupClassService#deleteUserRole(java.lang.String, java.lang.String) 
	*/
	@Override
	public void deleteUserRole(String userId, String roleCode) {
		//  Auto-generated method stub
		this.commonRoleDao.deleteUserRole(userId, roleCode);
	}

	/* (非 Javadoc) 
	* <p>Title: checkUserIsExist</p> 
	* <p>Description: </p> 
	* @param userId
	* @param roleCode
	* @return 
	* @see com.uws.training.service.IStartupClassService#checkUserIsExist(java.lang.String, java.lang.String) 
	*/
	@Override
	public boolean checkUserIsExist(String userId, String roleCode) {
		//  Auto-generated method stub
		return this.commonRoleDao.checkUserIsExist(userId, roleCode);
	}

	/* (非 Javadoc) 
	* <p>Title: checkClassCanOpen</p> 
	* <p>Description: </p> 
	* @param classId
	* @return 
	* @see com.uws.training.service.IStartupClassService#checkClassCanOpen(java.lang.String) 
	*/
	@Override
	public boolean checkClassCanOpen(String classId) {
		return this.startupClassDao.checkClassCanOpen(classId);
	}

	/* (非 Javadoc) 
	* <p>Title: updateClassFile</p> 
	* <p>Description: </p> 
	* @param fileId 
	* @see com.uws.training.service.IStartupClassService#updateClassFile(java.lang.String[]) 
	*/
	@Override
	public void updateClassFile(String classId,String[] fileId) {
		if (ArrayUtils.isEmpty(fileId))
		       fileId = new String[0];
		     List<UploadFileRef> list = this.fileUtil.getFileRefsByObjectId(classId);
		     for (UploadFileRef ufr : list) {
		       if (!ArrayUtils.contains(fileId, ufr.getUploadFile().getId()))
		         this.fileUtil.deleteFormalFile(ufr);
		    }
		     for (String id : fileId){
		       this.fileUtil.updateFormalFileTempTag(id, classId);
		    }
	}

	@Override
	public List<StartupClassApplyInfo> getStuApplyInfoByClassId(
			String startupClassId) {
		return this.startupClassDao.getStuApplyInfoByClassId(startupClassId);
	}

	@Override
	public List<StartupClassApplyInfo> getStuApplyClassListByStuId(
			String studentId) {
		return this.startupClassDao.getStuApplyClassListByStuId(studentId);
	}

	@Override
	public List<StartupClassInfo> getStartupClassByLeaderId(String leaderId) {
		return this.startupClassDao.getStartupClassByLeaderId(leaderId);
	}

	@Override
	public StartupClassApplyInfo getStartupClassApplyInfoBystuIdAndClassId(
			String stuId, String classInfoId) {
		return this.startupClassDao.getStartupClassApplyInfoBystuIdAndClassId(stuId, classInfoId);
	}

	@Override
	public Page queryMyStartClassInfoPage(StartupClassApplyInfo po, int pageNo,
			int pageSize) {
		return this.startupClassDao.queryMyStartClassInfoPage(po, pageNo, pageSize);
	}

}
