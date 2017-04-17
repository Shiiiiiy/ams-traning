/**   
* @Title: IStartupClassDao.java 
* @Package com.uws.training.dao.impl 
* @Description: TODO(用一句话描述该文件做什么) 
* @author zhangyb   
* @date 2015年10月21日 下午1:35:38 
* @version V1.0   
*/
package com.uws.training.dao;

import java.util.List;

import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.training.StartupClassApplyInfo;
import com.uws.domain.training.StartupClassInfo;
import com.uws.sys.model.Dic;

/** 
 * @ClassName: IStartupClassDao 
 * @Description: TODO 创业班管理DAO
 * @author zhangyb 
 * @date 2015年10月21日 下午1:35:38  
 */
public interface IStartupClassDao {

	/** 
	* @Title: queryStartupClassPage 
	* @Description: TODO 创业班维护列表查询方法
	* @param  startupClassInfo
	* @param  pageNo
	* @param  pageSize
	* @param  @return    
	* @return Page    
	* @throws 
	*/
	public Page queryStartupClassPage(StartupClassInfo startupClassInfo,int pageNo,int pageSize, String flag);
	
	/** 
	* @Title: saveStartupClass 
	* @Description: TODO 保存
	* @param  startupClassInfo
	* @param  fileId    
	* @return void    
	* @throws 
	*/
	public void saveStartupClass(StartupClassInfo startupClassInfo,String[] fileId);
	
	/** 
	* @Title: updateStartupClass 
	* @Description: TODO 更新
	* @param  startupClassInfo
	* @param  fileId    
	* @return void    
	* @throws 
	*/
	public void updateStartupClass(StartupClassInfo startupClassInfo,String[] fileId);
	
	/** 
	* @Title: delteStartupClass 
	* @Description: TODO 删除
	* @param  startupClassInfo    
	* @return void    
	* @throws 
	*/
	public void deleteStartupClass(StartupClassInfo startupClassInfo);
	
	/** 
	* @Title: getClassInfoById 
	* @Description: TODO 通过ID获取创业班信息对象
	* @param  @param classInfoId
	* @param  @return    
	* @return StartupClassInfo    
	* @throws 
	*/
	public StartupClassInfo getClassInfoById(String classInfoId);
	
	/** 
	* @Title: getClassInfoListByName 
	* @Description: TODO 通过name获取创业班信息list
	* @param  @param className
	* @param  @return    
	* @return List<StartupClassInfo>    
	* @throws 
	*/
	public List<StartupClassInfo> getClassInfoListByName(String className);
	
	/** 
	* @Title: queryStuApplyPage 
	* @Description: TODO 学生报名查询列表
	* @param  @param startupClassInfo
	* @param  @param pageNo
	* @param  @param pageSize
	* @param  @return    
	* @return Page    
	* @throws 
	*/
	public Page queryStuApplyPage(StartupClassInfo startupClassInfo,int pageNo,int pageSize,String userId,Dic applyStatus,Dic approveStatus);
	
	/** 
	* @Title: saveStuApplyInfo 
	* @Description: TODO 保存学生报名信息
	* @param  @param applyInfo    
	* @return void    
	* @throws 
	*/
	public void saveStuApplyInfo(StartupClassApplyInfo applyInfo);
	
	/** 
	* @Title: updateStuApplyInfo 
	* @Description: TODO 更新学生报名信息
	* @param  @param applyInfo    
	* @return void    
	* @throws 
	*/
	public void updateStuApplyInfo(StartupClassApplyInfo applyInfo);
	
	/** 
	* @Title: delStuApplyInfo 
	* @Description: TODO 删除学生报名信息
	* @param  @param applyInfo    
	* @return void    
	* @throws 
	*/
	public void delStuApplyInfo(StartupClassApplyInfo applyInfo);
	
	/** 
	* @Title: getStuApplyInfoById 
	* @Description: TODO get stu apply info by id
	* @param  @param id
	* @param  @return    
	* @return StartupClassApplyInfo    
	* @throws 
	*/
	public StartupClassApplyInfo getStuApplyInfoById(String id);
	
	/** 
	* @Title: queryStuApprovePage 
	* @Description: TODO 学生报名审核列表
	* @param  @param applyInfo
	* @param  @param pageNo
	* @param  @param pageSize
	* @param  @return    
	* @return Page    
	* @throws 
	*/
	public Page queryStuApprovePage(StartupClassApplyInfo applyInfo,int pageNo,int pageSize, String userId);
	
	/** 
	* @Title: getSubClassInfoList 
	* @Description: TODO 获取已提交的创业班list
	* @param  @return    
	* @return List<StartupClassInfo>    
	* @throws 
	*/
	public List<StartupClassInfo> getSubClassInfoList( Dic classStatus, String userId);
	
	/** 
	* @Title: checkClassCanOpen 
	* @Description: TODO 验证创业班是否能开班
	* @param  @param classId
	* @param  @return    
	* @return boolean    
	* @throws 
	*/
	public boolean checkClassCanOpen(String classId);

	/**
	 * 通过创业班id获得报名该班的学生信息
	 * @param startupClassId
	 * @return
	 */
	public List<StartupClassApplyInfo> getStuApplyInfoByClassId(
			String startupClassId);

	/**
	 * 通过学生id获取该学生所报名的创业班信息
	 * @param studentId
	 * @return
	 */
	public List<StartupClassApplyInfo> getStuApplyClassListByStuId(
			String studentId);

	/**
	 * 通过创业班班主任的id获得该班主任所带的创业班信息
	 * @param leaderId
	 * @return
	 */
	public List<StartupClassInfo> getStartupClassByLeaderId(String leaderId);

	/**
	 * 通过学生id和创业班id获取该学生的报名信息
	 * @param userId
	 * @param classInfoId
	 * @return
	 */
	public StartupClassApplyInfo getStartupClassApplyInfoBystuIdAndClassId(
			String stuId, String classInfoId);
	
	/** 
	* @Title: getStuApplyListByStuAndClass 
	* @Description: 通过学生ID和创业班ID获取创业班申请list
	* @param  @param studentId
	* @param  @param classInfoId
	* @param  @return    
	* @return List<StartupClassApplyInfo>    
	* @throws 
	*/
	public List<StartupClassApplyInfo> getStuApplyListByStuAndClass(String studentId,String classInfoId);

	/**
	 * 获取学生的创业班信息
	 * @param po
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public Page queryMyStartClassInfoPage(StartupClassApplyInfo po, int pageNo, int pageSize);

	
}
