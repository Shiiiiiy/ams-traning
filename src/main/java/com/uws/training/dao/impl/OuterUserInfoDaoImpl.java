package com.uws.training.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.uws.core.hibernate.dao.impl.BaseDaoImpl;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.util.DataUtil;
import com.uws.core.util.HqlEscapeUtil;
import com.uws.domain.training.OuterUserInfo;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.impl.DicFactory;
import com.uws.training.dao.IOuterUserInfoDao;

/**
 * @className OuterUserInfoDaoImpl.java
 * @package com.uws.training.dao.impl
 * @description
 * @author 联合永道
 * @date 2015年10月15日 17:55:33
 */
@Repository("com.uws.training.dao.impl.OuterUserInfoDaoImpl")
public class OuterUserInfoDaoImpl extends BaseDaoImpl implements IOuterUserInfoDao {
	
	@Autowired
	private static DicUtil dicUtil = DicFactory.getDicUtil();
	@Override
	public Page queryPageOuterUserInfo(OuterUserInfo po, int pageNo) {
		String hql = "from OuterUserInfo where 1=1 ";
		Map<String, Object> params = new HashMap<String, Object>();
		//姓名
		if(DataUtil.isNotNull(po) && DataUtil.isNotNull(po.getUserName())) {
			hql += "and userName like :userName ";
			if(HqlEscapeUtil.IsNeedEscape(po.getUserName())) {
				params.put("userName", "%"+HqlEscapeUtil.escape(po.getUserName())+"%");
				hql += HqlEscapeUtil.HQL_ESCAPE;
			}else {
				params.put("userName", "%"+po.getUserName()+"%");
			}
		}
		//编码
		if(DataUtil.isNotNull(po) && DataUtil.isNotNull(po.getUserCode())) {
			hql += "and userCode like :userCode ";
			if(HqlEscapeUtil.IsNeedEscape(po.getUserCode())) {
				params.put("userCode", "%"+HqlEscapeUtil.escape(po.getUserCode())+"%");
				hql += HqlEscapeUtil.HQL_ESCAPE;
			}else {
				params.put("userCode", "%"+po.getUserCode()+"%");
			}
		}
		//性别
		if(DataUtil.isNotNull(po) && DataUtil.isNotNull(po.getGender()) && DataUtil.isNotNull(po.getGender().getId())) {
			hql += "and gender.id = :genderId ";
			params.put("genderId", po.getGender().getId());
		}
		//类型
		if(DataUtil.isNotNull(po) && DataUtil.isNotNull(po.getUserType()) && DataUtil.isNotNull(po.getUserType().getId())) {
			hql += "and userType.id = :userTypeId ";
			params.put("userTypeId", po.getUserType().getId());
		}
		//启用状态
		if(DataUtil.isNotNull(po) && DataUtil.isNotNull(po.getUserStatus()) && DataUtil.isNotNull(po.getUserStatus().getId())) {
			hql += "and userStatus.id =:userStatusId ";
			params.put("userStatusId", po.getUserStatus().getId());
		}
		hql += "order by createTime desc";
		return this.pagedQuery(hql, params, Page.DEFAULT_PAGE_SIZE, pageNo);
	}

	@Override
	public Long outerUserUsedCount(String userId) {
		
		String hql = "select count(*) from SeminarManage t where t.outerUserId.id = ? ";
		String hql2 = "select count(*) from AdvisorInfo t where t.outerUserInfo.id = ? ";
		return this.queryCount(hql, new Object[]{userId}) + this.queryCount(hql2, new Object[]{userId});
	}

	/* (非 Javadoc) 
	* <p>Title: getAllOuterUserList</p> 
	* <p>Description: </p> 
	* @return 
	* @see com.uws.training.dao.IOuterUserInfoDao#getAllOuterUserList() 
	*/
	@SuppressWarnings("unchecked")
	@Override
	public List<OuterUserInfo> getAllOuterUserList() {
		String userStatusId = dicUtil.getDicInfo("STATUS_ENABLE_DISABLE", "ENABLE").getId();
		String hql = "from OuterUserInfo o where o.userStatus.id =?";
		return this.query(hql, new Object[]{userStatusId});
	}
}