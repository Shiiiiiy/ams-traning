/**   
* @Title: IStartupClassService.java 
* @Package com.uws.training.service 
* @Description: (用一句话描述该文件做什么) 
* @author zhangyb   
* @date 2015年10月21日 下午1:30:20 
* @version V1.0   
*/
package com.uws.training.service;

import java.util.List;

import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.training.StartupClassApplyInfo;
import com.uws.domain.training.StartupClassInfo;
import com.uws.sys.model.Dic;

/** 
 * @ClassName: IStartupClassService 
 * @Description:  创业班管理
 * @author zhangyb 
 * @date 2015年10月21日 下午1:30:20  
 */
public interface IStartupClassService {

	/** 
	* @Title: queryStartupClassPage 
	* @Description:  创业班维护列表查询方法
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
	* @Description:  保存
	* @param  startupClassInfo
	* @param  fileId    
	* @return void    
	* @throws 
	*/
	public void saveStartupClass(StartupClassInfo startupClassInfo,String[] fileId);
	
	/** 
	* @Title: updateStartupClass 
	* @Description:  更新
	* @param  startupClassInfo
	* @param  fileId    
	* @return void    
	* @throws 
	*/
	public void updateStartupClass(StartupClassInfo startupClassInfo,String[] fileId);
	public void updateStartupClass(StartupClassInfo startupClassInfo);
	
	/** 
	* @Title: delteStartupClass 
	* @Description:  删除
	* @param  startupClassInfo    
	* @return void    
	* @throws 
	*/
	public void deleteStartupClass(StartupClassInfo startupClassInfo);
	
	/** 
	* @Title: getStartupClassById 
	* @Description:  通过ID获取创业班信息对象
	* @param  @param classInfoId    
	* @return void    
	* @throws 
	*/
	public StartupClassInfo getStartupClassById(String classInfoId);
	
	/** 
	* @Title: getClassInfoListByName 
	* @Description:  通过name获取创业班信息list
	* @param  @param className
	* @param  @return    
	* @return List<StartupClassInfo>    
	* @throws 
	*/
	public List<StartupClassInfo> getClassInfoListByName(String className);
	
	/** 
	* @Title: queryStuApplyPage 
	* @Description:  学生报名查询列表
	* @param  @param startupClassInfo
	* @param  @param pageNo
	* @param  @param pageSize
	* @param  @return    
	* @return Page    
	* @throws 
	*/
	public Page queryStuApplyPage(StartupClassInfo startupClassInfo,int pageNo,int pageSize,
			String userId,Dic applyStatus,Dic approveStatus);
	
	/** 
	* @Title: saveStuApplyInfo 
	* @Description:  保存学生报名信息
	* @param  @param applyInfo    
	* @return void    
	* @throws 
	*/
	public void saveStuApplyInfo(StartupClassApplyInfo applyInfo);
	
	/** 
	* @Title: updateStuApplyInfo 
	* @Description:  更新学生报名信息
	* @param  @param applyInfo    
	* @return void    
	* @throws 
	*/
	public void updateStuApplyInfo(StartupClassApplyInfo applyInfo);
	
	/** 
	* @Title: delStuApplyInfo 
	* @Description:  删除学生报名信息
	* @param  @param applyInfo    
	* @return void    
	* @throws 
	*/
	public void delStuApplyInfo(StartupClassApplyInfo applyInfo);
	
	/** 
	* @Title: getStuApplyInfoById 
	* @Description:  get stu apply info by id
	* @param  @param id
	* @param  @return    
	* @return StartupClassApplyInfo    
	* @throws 
	*/
	public StartupClassApplyInfo getStuApplyInfoById(String id);
	
	/** 
	* @Title: queryStuApprovePage 
	* @Description:  学生报名审核列表
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
	* @Description:  获取已提交的创业班list
	* @param  @return    
	* @return List<StartupClassInfo>    
	* @throws 
	*/
	public List<StartupClassInfo> getSubClassInfoList( Dic classStatus, String userId);
	
	/**
	 * 
	 * @Title: updateUserRole
	 * @Description: 更新角色信息
	 * @param orginUserId
	 * @param updateUserId
	 * @param roleCode
	 * @throws
	 */
	public void updateUserRole(String orginUserId,String updateUserId,String roleCode);
	
	/**
	 * 
	 * @Title: saveUserRole
	 * @Description: 保存角色信息
	 * @param userId
	 * @param roleCode
	 * @throws
	 */
	public void saveUserRole(String userId,String roleCode);
	
	/**
	 * 
	 * @Title: saveUserRole
	 * @Description: 【物理删除】 删除角色信息
	 * @param userId
	 * @param roleCode
	 * @throws
	 */
	public void deleteUserRole(String userId,String roleCode);
	
	/**
	 * 
	 * @Title: checkUserIsExist
	 * @Description: 判断角色定义是否存在
	 * @param userId
	 * @param roleCode
	 * @return
	 * @throws
	 */
	public boolean checkUserIsExist(String userId,String roleCode);
	
	/** 
	* @Title: checkClassCanOpen 
	* @Description:  验证创业班是否能开班
	* @param  @param classId
	* @param  @return    
	* @return boolean    
	* @throws 
	*/
	public boolean checkClassCanOpen(String classId);
	
	/** 
	* @Title: updateClassFile 
	* @Description: 更新课程附件
	* @param  @param fileId    
	* @return void    
	* @throws 
	*/
	public void updateClassFile(String classId,String[] fileId);

	/**
	 * 通过创业班班级id获得报名该班的学生信息
	 * @param startupClassId
	 * @return
	 */
	public List<StartupClassApplyInfo> getStuApplyInfoByClassId(
			String startupClassId);

	/**
	 * 通过学生id获得该学生报名所有创业班的信息
	 * @param studentId
	 * @return
	 */
	public List<StartupClassApplyInfo> getStuApplyClassListByStuId(String studentId);

	/**
	 * 通过创业班班主任id获取该班主任所带的创业班信息
	 * @param id
	 * @return
	 */
	public List<StartupClassInfo> getStartupClassByLeaderId(String leaderId);
	
	/**
	 * 通过创业班id和学生id获取学生的报名信息
	 * @param stuId
	 * @param classInfoId
	 * @return
	 */
	public StartupClassApplyInfo getStartupClassApplyInfoBystuIdAndClassId(
			String stuId, String classInfoId);

	/**
	 * 查询报名信息表，获取学生报名创业班的信息
	 * @param po
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public Page queryMyStartClassInfoPage(StartupClassApplyInfo po, int pageNo, int pageSize);
}
