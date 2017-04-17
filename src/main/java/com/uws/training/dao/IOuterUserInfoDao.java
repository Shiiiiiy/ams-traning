package com.uws.training.dao;
import java.util.List;

import com.uws.core.hibernate.dao.IBaseDao;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.training.OuterUserInfo;

public interface IOuterUserInfoDao extends IBaseDao {

	/**
	 * 查询OuterUserInfo表获取数据
	 * @param po
	 * @param pageNo
	 * @return page
	 */
	Page queryPageOuterUserInfo(OuterUserInfo po, int pageNo);

	/**
	 * 通过校外师资的id查询被使用的次数
	 * @param userId
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