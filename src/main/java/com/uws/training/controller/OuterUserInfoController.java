package com.uws.training.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uws.core.base.BaseController;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.util.DataUtil;
import com.uws.domain.training.OuterUserInfo;
import com.uws.sys.model.Dic;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.FileUtil;
import com.uws.sys.service.impl.DicFactory;
import com.uws.sys.service.impl.FileFactory;
import com.uws.training.service.IOuterUserInfoService;
import com.uws.training.util.TrainingConstants;

/**
 * @className OuterUserInfoController.java
 * @package com.uws.training.controller
 * @description
 * @author 联合永道
 * @date 2015年10月15日 17:56:43
 */
@Controller
public class OuterUserInfoController extends BaseController {
	@Autowired
	private IOuterUserInfoService outerUserInfoService;
	
	private FileUtil fileUtil = FileFactory.getFileUtil();
	private DicUtil dicUtil = DicFactory.getDicUtil();
	
	/**
	 * 讲师导师信息列表
	 * @param model
	 * @param request
	 * @param po
	 * @return page
	 */
	@RequestMapping({TrainingConstants.TRAINING_MAINTAIN + "/opt-query/outerUserInfo"})
	public String outerUserInfo(ModelMap model, HttpServletRequest request, OuterUserInfo po) {
		//性别
		List<Dic> gender = dicUtil.getDicInfoList("GENDER");
		model.addAttribute("gender", gender);
		//讲师导师类型
		List<Dic> userType = dicUtil.getDicInfoList("ADVISOR_TYPE");
		model.addAttribute("userType", userType);
		//启用禁用状态
		List<Dic> userStatus = dicUtil.getDicInfoList("STATUS_ENABLE_DISABLE");
		model.addAttribute("userStatus", userStatus);
		String pageNo = request.getParameter("pageNo");
		pageNo = pageNo != null ? pageNo : "1";
		Page page = outerUserInfoService.queryPageOuterUserInfo(po,Integer.parseInt(pageNo));
		model.addAttribute("po", po);
		model.addAttribute("page", page);
		return TrainingConstants.TRAINING_COMMON + "/outerUserInfoList";
	}
	/**
	 * 编辑或者更新讲师导师信息
	 * @param model
	 * @param request
	 * @param po
	 * @return
	 */
	@RequestMapping({TrainingConstants.TRAINING_MAINTAIN + "/opt-add/outerUserInfo", TrainingConstants.TRAINING_MAINTAIN + "/opt-update/outerUserInfo"})
	public String editInfo(ModelMap model, HttpServletRequest request, OuterUserInfo po) {
		List<Dic> gender = dicUtil.getDicInfoList("GENDER");
		List<Dic> advisor = dicUtil.getDicInfoList("ADVISOR_TYPE");
		List<Dic> userStatus = dicUtil.getDicInfoList("STATUS_ENABLE_DISABLE");
		List<Dic> projectList = this.dicUtil.getDicInfoList("TRAINING_TEACHER_PROJECT");
		model.addAttribute("projectList", projectList);
		model.addAttribute("userStatus", userStatus);
		model.addAttribute("advisor", advisor);
		model.addAttribute("gender", gender);
		OuterUserInfo vo = null;
		if(DataUtil.isNotNull(po.getId())) {
			vo = outerUserInfoService.getOuterUserInfoById(po.getId());
			model.addAttribute("listFile", this.fileUtil.getFileRefsByObjectId(po.getId()));
			int lfLength = this.fileUtil.getFileRefsByObjectId(po.getId()).size();
			model.addAttribute("lfLength", lfLength);
			model.addAttribute("vo", vo);
		}
		return TrainingConstants.TRAINING_COMMON + "/outerUserInfoEdit";
	}
	
	/**
	 * 存储讲师导师信息
	 * @param model
	 * @param request
	 * @param po
	 * @return
	 */
	@RequestMapping({TrainingConstants.TRAINING_MAINTAIN + "/opt-save/outerUserInfo"})
	public String saveInfo(ModelMap model, HttpServletRequest request, OuterUserInfo po, String[] fileId, String flag) {
		if(DataUtil.isNotNull(po) && DataUtil.isNotNull(po.getId())) {
			OuterUserInfo outerUserInfo = outerUserInfoService.getOuterUserInfoById(po.getId());
			Date createTime = outerUserInfo.getCreateTime();
			BeanUtils.copyProperties(po, outerUserInfo);
			outerUserInfo.setCreateTime(createTime);
			if(outerUserInfo.getParticipateProject() != null) {
				outerUserInfo.setParticipateProjectStr(outerUserInfo.getParticipateProject().getCode());
			}
			outerUserInfoService.updateOuterUserInfo(outerUserInfo, fileId);
		}else {
			String userCode = new SimpleDateFormat("yyMMddHHmmss").format(new java.util.Date());
			po.setUserCode(userCode);
			if(po.getParticipateProject() != null) {
				po.setParticipateProjectStr(po.getParticipateProject().getCode());
			}
			outerUserInfoService.saveOuterUserInfo(po, fileId);
		}
		return "redirect:" + TrainingConstants.TRAINING_MAINTAIN + "/opt-query/outerUserInfo.do";
	}
	/**
	 * 查看讲师导师信息
	 * @param model
	 * @param request
	 * @param po
	 * @return
	 */
	@RequestMapping({TrainingConstants.TRAINING_MAINTAIN + "/nsm/outerUserInfo", TrainingConstants.TRAINING_SEMINAR + "/nsm/viewTeacherInfo"})
	public String viewInfo(ModelMap model, HttpServletRequest request, OuterUserInfo po) {
		OuterUserInfo vo = this.outerUserInfoService.getOuterUserInfoById(po.getId());
		model.addAttribute("vo", vo);
		model.addAttribute("listFile", this.fileUtil.getFileRefsByObjectId(po.getId()));
		String flag = "true";
		if(!(this.fileUtil.getFileRefsByObjectId(po.getId()).size()>0)) {
			flag = "false";
		}
		String projectCode = vo.getParticipateProjectStr();
		String projectName = "";
		if(projectCode != null && projectCode.indexOf(",") > 0 && projectCode.split(",").length > 0) {
			for(String s : projectCode.split(",")) {
				projectName = projectName + this.dicUtil.getDicInfo("TRAINING_TEACHER_PROJECT", s).getName() + ",";
			}
		}
		model.addAttribute("flag", flag);
		model.addAttribute("projectName", DataUtil.isNotNull(projectName) ? projectName.substring(0, projectName.lastIndexOf(",")) : "");
		return TrainingConstants.TRAINING_COMMON + "/outerUserInfoView";
	}
	
	/**
	 * 禁用某校外师资
	 * @param model
	 * @param request
	 * @param id
	 * @return
	 */
	@RequestMapping({"/training/maintain/opt-dis/disableInfo.do"})
	public String disableInfo(ModelMap model, HttpServletRequest request, String id, String useFlag) {
		//用户状态为禁用
		Dic disable = dicUtil.getDicInfo("STATUS_ENABLE_DISABLE", "DISABLE");
		//用户状态为启用
		Dic able = dicUtil.getDicInfo("STATUS_ENABLE_DISABLE", "ENABLE");
		if(DataUtil.isNotNull(id)) {
			OuterUserInfo outerUserInfo = outerUserInfoService.getOuterUserInfoById(id);
			if("0".equals(useFlag)) {
				outerUserInfo.setUserStatus(able);
			}else {
				outerUserInfo.setUserStatus(disable);
			}
			outerUserInfoService.updateOuterUserInfo(outerUserInfo, null);
		}
		return "redirect:" + TrainingConstants.TRAINING_MAINTAIN + "/opt-query/outerUserInfo.do";
	}
	
	/**
	 * 判断可不可物理删除
	 * @param model
	 * @param request
	 * @param id
	 * @param teacherType
	 * @return
	 */
	@RequestMapping(value={"training/check/opt-del/delOuterUserInfo"},produces = {"text/plain;charset=UTF-8"})
	@ResponseBody
	public String checkDel(ModelMap model, HttpServletRequest request, String id) {
		Long count = outerUserInfoService.outerUserUsedCount(id);
		if(count==0) {
			//可以物理删除
			return "0";
		}else {
			return "1";
		}
	}
	
	/**
	 * 删除导师讲师信息
	 * @param model
	 * @param request
	 * @param po
	 * @return
	 */
	@RequestMapping({"training/maintain/opt-del/delOuterUserInfo"})
	@ResponseBody
	public String delInfo(ModelMap model, HttpServletRequest request, String id){
		this.outerUserInfoService.delOuterUserInfo(id);
		return "success";
	}
}