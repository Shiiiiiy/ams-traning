package com.uws.training.dao;

import java.util.Date;
import java.util.List;

import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.training.AdvisorApplyInfo;
import com.uws.domain.training.AdvisorInfo;

/** 
 * @ClassName: IAdvisorApplyDao 
 * @Description: 导师预约管理DAO 
 * @author zhangyb 
 * @date 2015年10月21日 下午1:26:28  
 */
public interface IAdvisorApplyDao {

	/** 
	* @Title: queryAdvisorInfoPage 
	* @Description:  导师维护查询列表
	* @param  @param advisorInfo
	* @param  @param pageNo
	* @param  @param pageSize
	* @param  @return    
	* @return Page    
	* @throws 
	*/
	public Page queryAdvisorInfoPage(AdvisorInfo advisorInfo,int pageNo,int pageSize);
	
	/** 
	* @Title: saveAdvisorInfo 
	* @Description:  保存
	* @param  @param advisorInfo    
	* @return void    
	* @throws 
	*/
	public void saveAdvisorInfo(AdvisorInfo advisorInfo);
	
	/** 
	* @Title: updateAdvisorInfo 
	* @Description:  更新
	* @param  @param advisorInfo    
	* @return void    
	* @throws 
	*/
	public void updateAdvisorInfo(AdvisorInfo advisorInfo);
	
	/** 
	* @Title: deleteAdvisorInfo 
	* @Description:  删除
	* @param  @param advisorInfo    
	* @return void    
	* @throws 
	*/
	public void deleteAdvisorInfo(AdvisorInfo advisorInfo);
	
	/** 
	* @Title: getAdvisorById 
	* @Description:  getAdvisorById
	* @param  @param id
	* @param  @return    
	* @return AdvisorInfo    
	* @throws 
	*/
	public AdvisorInfo getAdvisorById(String id);
	
	/** 
	* @Title: queryAdvisorApplyPage 
	* @Description:  导师预约列表页
	* @param  @param advisorInfo
	* @param  @param pageNo
	* @param  @param pageSize
	* @param  @return    
	* @return Page    
	* @throws 
	*/
	public Page queryAdvisorApplyPage(AdvisorApplyInfo advisorApply,int pageNo,int pageSize);
	
	/** 
	* @Title: saveAdvisorApply 
	* @Description:  save
	* @param  @param advisorApplyInfo    
	* @return void    
	* @throws 
	*/
	public void saveAdvisorApply(AdvisorApplyInfo advisorApplyInfo);
	
	/** 
	* @Title: updateAdvisorApply 
	* @Description:  update
	* @param  @param advisorApplyInfo    
	* @return void    
	* @throws 
	*/
	public void updateAdvisorApply(AdvisorApplyInfo advisorApplyInfo);
	
	/** 
	* @Title: deleteAdvisorApply 
	* @Description:  删除
	* @param  @param advisorApplyInfo    
	* @return void    
	* @throws 
	*/
	public void deleteAdvisorApply(AdvisorApplyInfo advisorApplyInfo);
	
	/** 
	* @Title: getAdvisorApplyById 
	* @Description:  getAdvisorApplyById
	* @param  @param id
	* @param  @return    
	* @return AdvisorApplyInfo    
	* @throws 
	*/
	public AdvisorApplyInfo getAdvisorApplyById(String id);
	
	/** 
	* @Title: getAdvisorListByUserId 
	* @Description: 验证当天导师是否已有值班信息
	* @param  @param userId
	* @param  @param date
	* @param  @return    
	* @return List<AdvisorInfo>    
	* @throws 
	*/
	public abstract List<AdvisorInfo> getAdvisorListByUserId(String userId,Date date);
}
