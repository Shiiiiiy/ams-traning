package com.uws.training.service;

import java.util.List;

import com.uws.core.base.IBaseService;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.training.OuterUserInfo;

public interface IOuterUserInfoService extends IBaseService {

	/**
	 * 查询outerUserInfo表
	 * @param po
	 * @param pageNo
	 * @return page
	 */
	Page queryPageOuterUserInfo(OuterUserInfo po, int pageNo);

	/**
	 * 通过id查询数据
	 * @param id
	 * @return OuterUserInfo
	 */
	OuterUserInfo getOuterUserInfoById(String id);

	/**
	 * 保存数据
	 * @param po
	 */
	void saveOuterUserInfo(OuterUserInfo po, String[] fileId);

	/**
	 * 更新数据
	 * @param po
	 */
	void updateOuterUserInfo(OuterUserInfo po, String[] fileId);

	/**
	 * 删除数据
	 * @param id
	 */
	void delOuterUserInfo(String id);
	
	/**
	 * 通过校外师资的id查询被使用的次数
	 * @param userId
	 * @param userType
	 * @return
	 */
	Long outerUserUsedCount(String userId);
	
	/** 
	* @Title: getAllOuterUserList 
	* @Description: 获取所有的校外师资
	* @param  @return    
	* @return List<OuterUserInfo>    
	* @throws 
	*/
	public List<OuterUserInfo> getAllOuterUserList();

}