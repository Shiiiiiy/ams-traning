
package com.uws.training.service;

import java.util.Date;
import java.util.List;

import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.training.AdvisorApplyInfo;
import com.uws.domain.training.AdvisorInfo;

/**   
* @Title: IAdvisorApplyService.java 
* @Package com.uws.training.service 
* @Description: (导师预约管理service) 
* @author zhangyb   
* @date 2015年10月21日 下午1:20:07 
* @version V1.0   
*/
public interface IAdvisorApplyService {

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
	public abstract Page queryAdvisorInfoPage(AdvisorInfo advisorInfo,int pageNo,int pageSize);
	
	/** 
	* @Title: saveAdvisorInfo 
	* @Description:  保存
	* @param  @param advisorInfo    
	* @return void    
	* @throws 
	*/
	public abstract void saveAdvisorInfo(AdvisorInfo advisorInfo,String[] fileId);
	
	/** 
	* @Title: updateAdvisorInfo 
	* @Description:  更新
	* @param  @param advisorInfo    
	* @return void    
	* @throws 
	*/
	public abstract void updateAdvisorInfo(AdvisorInfo advisorInfo,String[] fileId);
	
	/** 
	* @Title: updateAdvisorInfo 
	* @Description:  更新
	* @param  @param advisorInfo    
	* @return void    
	* @throws 
	*/
	public abstract void updateAdvisorInfo(AdvisorInfo advisorInfo);
	
	/** 
	* @Title: deleteAdvisorInfo 
	* @Description:  删除
	* @param  @param advisorInfo    
	* @return void    
	* @throws 
	*/
	public abstract void deleteAdvisorInfo(AdvisorInfo advisorInfo);
	
	/** 
	* @Title: getAdvisorById 
	* @Description:  getAdvisorById
	* @param  @param id
	* @param  @return    
	* @return AdvisorInfo    
	* @throws 
	*/
	public abstract AdvisorInfo getAdvisorById(String id);
	
	/** 
	* @Title: queryAdvisorApplyPage 
	* @Description:  学生预约列表页
	* @param  @param advisorInfo
	* @param  @param pageNo
	* @param  @param pageSize
	* @param  @return    
	* @return Page    
	* @throws 
	*/
	public abstract Page queryAdvisorApplyPage(AdvisorApplyInfo advisorApply,int pageNo,int pageSize);
	
	/** 
	* @Title: saveAdvisorApply 
	* @Description:  save
	* @param  @param advisorApplyInfo    
	* @return void    
	* @throws 
	*/
	public abstract void saveAdvisorApply(AdvisorApplyInfo advisorApplyInfo);
	
	/** 
	* @Title: updateAdvisorApply 
	* @Description:  update
	* @param  @param advisorApplyInfo    
	* @return void    
	* @throws 
	*/
	public abstract void updateAdvisorApply(AdvisorApplyInfo advisorApplyInfo);
	
	/** 
	* @Title: deleteAdvisorApply 
	* @Description:  删除
	* @param  @param advisorApplyInfo    
	* @return void    
	* @throws 
	*/
	public abstract void deleteAdvisorApply(AdvisorApplyInfo advisorApplyInfo);
	
	/** 
	* @Title: getAdvisorApplyById 
	* @Description:  getAdvisorApplyById
	* @param  @param id
	* @param  @return    
	* @return AdvisorApplyInfo    
	* @throws 
	*/
	public abstract AdvisorApplyInfo getAdvisorApplyById(String id);
	
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
