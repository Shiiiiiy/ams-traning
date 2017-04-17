
package com.uws.training.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uws.common.service.IStudentCommonService;
import com.uws.common.util.SchoolYearUtil;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.session.SessionFactory;
import com.uws.core.session.SessionUtil;
import com.uws.core.util.DataUtil;
import com.uws.domain.orientation.StudentInfoModel;
import com.uws.domain.training.AdvisorApplyInfo;
import com.uws.domain.training.AdvisorInfo;
import com.uws.domain.training.OuterUserInfo;
import com.uws.sys.model.Dic;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.FileUtil;
import com.uws.sys.service.impl.DicFactory;
import com.uws.sys.service.impl.FileFactory;
import com.uws.training.service.IAdvisorApplyService;
import com.uws.training.service.IOuterUserInfoService;
import com.uws.training.service.ISeminarService;
import com.uws.user.model.User;
import com.uws.util.ProjectSessionUtils;
/**   
* @Title: AdvisorApplyController.java 
* @Package com.uws.training.controller 
* @Description: (导师预约管理) 
* @author zhangyb   
* @date 2015年10月21日 下午1:18:30 
* @version V1.0   
*/
@Controller
public class AdvisorApplyController {
	@Autowired
	private DicUtil dicUtil = DicFactory.getDicUtil();
	private SessionUtil sessionUtil = SessionFactory.getSession(null);
	private FileUtil fileUtil=FileFactory.getFileUtil();
	@Autowired
	private IStudentCommonService studentCommonService;
	@Autowired
	private IAdvisorApplyService advisorApplyService;
	@Autowired
	private ISeminarService seminarService;
	@Autowired
	private IOuterUserInfoService outerUserInfoService;
	
	/** 
	* @Title: queryAdvisorPage 
	* @Description:  导师维护列表页
	* @param  @param model
	* @param  @param request
	* @param  @param advisorInfo
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/training/setadvisor/opt-query/queryAdvisorPage.do"})
	public String queryAdvisorPage(ModelMap model,HttpServletRequest request,
			AdvisorInfo advisorInfo) {
		String pageNo=request.getParameter("pageNo") !=null? request.getParameter("pageNo"):"1";
		List<Dic> statusList = this.dicUtil.getDicInfoList("ADVISOR_STATUS");
		List<Dic> schoolYearList = this.dicUtil.getDicInfoList("YEAR");
		Page page = this.advisorApplyService.queryAdvisorInfoPage(advisorInfo, Integer.parseInt(pageNo), Page.DEFAULT_PAGE_SIZE);
		model.addAttribute("advisorInfo", advisorInfo);
		model.addAttribute("page", page);
		model.addAttribute("statusList", statusList);
		model.addAttribute("schoolYearList", schoolYearList);
		return "training/advisor/queryAdvisorList";
	}
	
	/** 
	* @Title: editAdvisorInfo 
	* @Description:  新增修改导师预约信息
	* @param  @param model
	* @param  @param request
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/training/setadvisor/opt-edit/editAdvisorInfo.do"})
	public String editAdvisorInfo(ModelMap model,HttpServletRequest request) {
		String id = request.getParameter("id");
		if(DataUtil.isNotNull(id)) {     //更新
			AdvisorInfo advisorInfo = this.advisorApplyService.getAdvisorById(id);
			model.addAttribute("advisorInfo", advisorInfo);
			model.addAttribute("uploadFileRefList", this.fileUtil.getFileRefsByObjectId(id));		
		}else{
			model.addAttribute("advisorInfo", new AdvisorInfo());
		}
		List<OuterUserInfo> outerUserList = this.outerUserInfoService.getAllOuterUserList();
		model.addAttribute("outerUserList", outerUserList);
		return "training/advisor/editAdvisorInfo";
	}
	
	/** 
	* @Title: saveAdvisorInfo 
	* @Description:  保存新增或修改
	* @param  @param model
	* @param  @param request
	* @param  @param advisorInfo
	* @param  @param fileId
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/training/setadvisor/opt-save/saveAdvisorInfo.do"})
	public String saveAdvisorInfo(ModelMap model,HttpServletRequest request,AdvisorInfo advisorInfo,
			String[] fileId) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if(DataUtil.isNotNull(advisorInfo.getId())) {   // 保存更新
			AdvisorInfo oldAdvisor = this.advisorApplyService.getAdvisorById(advisorInfo.getId());
			try {
				Date date = sdf.parse(advisorInfo.getAdvisorDateStr());
				advisorInfo.setAdvisorDate(date);
			} catch (ParseException e) {
				//  Auto-generated catch block
				e.printStackTrace();
			}
			BeanUtils.copyProperties(advisorInfo, oldAdvisor, new String[]{"advisorStuNum","advisorStatus","schoolYear","createTime","updateTime","creator"});
			this.advisorApplyService.updateAdvisorInfo(oldAdvisor, fileId);
		}else{                                         //保存新增
			Dic statusDic = this.dicUtil.getDicInfo("ADVISOR_STATUS", "SAVED");
			Dic curYear = SchoolYearUtil.getYearDic();
			String userId = this.sessionUtil.getCurrentUserId();
			User user = new User();
			user.setId(userId);
			try {
				Date date = sdf.parse(advisorInfo.getAdvisorDateStr());
				advisorInfo.setAdvisorDate(date);
			} catch (ParseException e) {
				//  Auto-generated catch block
				e.printStackTrace();
			}
			advisorInfo.setCreator(user);
			advisorInfo.setAdvisorStatus(statusDic);
			advisorInfo.setSchoolYear(curYear);
			advisorInfo.setAdvisorStuNum(0);
			this.advisorApplyService.saveAdvisorInfo(advisorInfo, fileId);
		}
		return "redirect:/training/setadvisor/opt-query/queryAdvisorPage.do";
	}
	
	/** 
	* @Title: subAdvisorInfo 
	* @Description:  提交导师值班信息 
	* @param  @param model
	* @param  @param request
	* @param  @param advisorInfo
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/training/setadvisor/opt-save/pubAdvisorInfo.do"})
	public String subAdvisorInfo(ModelMap model,HttpServletRequest request,AdvisorInfo advisorInfo,String[] fileId) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Dic statusDic = this.dicUtil.getDicInfo("ADVISOR_STATUS", "PUBLISHED");
		String id = request.getParameter("advisorId");
		String flag = request.getParameter("flag");
		if(flag.equals("publish")) {
			AdvisorInfo ai = this.advisorApplyService.getAdvisorById(id);
			ai.setAdvisorStatus(statusDic);
			this.advisorApplyService.updateAdvisorInfo(ai, fileId);
		}else {
			if(DataUtil.isNotNull(advisorInfo.getId())) {   // 提交更新
				AdvisorInfo oldAdvisor = this.advisorApplyService.getAdvisorById(advisorInfo.getId());
				try {
					Date date = sdf.parse(advisorInfo.getAdvisorDateStr());
					advisorInfo.setAdvisorDate(date);
				} catch (ParseException e) {
					//  Auto-generated catch block
					e.printStackTrace();
				}
				BeanUtils.copyProperties(advisorInfo, oldAdvisor, new String[]{"advisorStuNum","advisorStatus","schoolYear","createTime","updateTime","creator"});
				oldAdvisor.setAdvisorStatus(statusDic);
				this.advisorApplyService.updateAdvisorInfo(oldAdvisor, fileId);
			}else{                                         //提交新增
				Dic curYear = SchoolYearUtil.getYearDic();
				String userId = this.sessionUtil.getCurrentUserId();
				User user = new User();
				user.setId(userId);
				try {
					Date date = sdf.parse(advisorInfo.getAdvisorDateStr());
					advisorInfo.setAdvisorDate(date);
				} catch (ParseException e) {
					//  Auto-generated catch block
					e.printStackTrace();
				}
				advisorInfo.setCreator(user);
				advisorInfo.setAdvisorStatus(statusDic);
				advisorInfo.setSchoolYear(curYear);
				advisorInfo.setAdvisorStuNum(0);
				this.advisorApplyService.saveAdvisorInfo(advisorInfo, fileId);
			}
		}
		
		
		return "redirect:/training/setadvisor/opt-query/queryAdvisorPage.do";
	}
	
	/** 
	* @Title: delAdvisorInfo 
	* @Description:  删除导师值班信息
	* @param  @param model
	* @param  @param request
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/training/setadvisor/opt-del/delAdvisorInfo.do"})
	public String delAdvisorInfo(ModelMap model,HttpServletRequest request) {
		String id = request.getParameter("id");
		AdvisorInfo advisorInfo = this.advisorApplyService.getAdvisorById(id);
		this.advisorApplyService.deleteAdvisorInfo(advisorInfo);
		return "redirect:/training/setadvisor/opt-query/queryAdvisorPage.do";
	}
	
	/** 
	* @Title: checkAdvisorInfo 
	* @Description: 验证导师预约记录是否重复
	* @param  @param outerUserId
	* @param  @param advisorDate
	* @param  @param id
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/training/setadvisor/opt-query/checkAdvisorInfo.do"})
	@ResponseBody
	public String checkAdvisorInfo(@RequestParam String outerUserId,@RequestParam String advisorDate,
			@RequestParam String id) {
		String result = "true";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date date = sdf.parse(advisorDate);
			List<AdvisorInfo> advisorList = this.advisorApplyService.getAdvisorListByUserId(outerUserId, date);
			if(DataUtil.isNotNull(id)) {  //更新时
				for(AdvisorInfo info : advisorList) {
					System.out.println(sdf.format(info.getAdvisorDate()));
					if(!(DataUtil.isEquals(info.getId(), id)) &&DataUtil.isEquals(sdf.format(info.getAdvisorDate()), advisorDate)) {
						result = "false";
					}
				}
			}else{                       //新增时
				if(advisorList.size() > 0) {
					result = "false";
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/** 
	* @Title: viewAdvisorInfo 
	* @Description: 查看导师预约设置 
	* @param  @param model
	* @param  @param request
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/training/setadvisor/opt-view/viewAdvisorInfo.do"})
	public String viewAdvisorInfo(ModelMap model,HttpServletRequest request) {
		String id = request.getParameter("id");
		AdvisorInfo info = this.advisorApplyService.getAdvisorById(id);
		model.addAttribute("advisorInfo", info);
		model.addAttribute("uploadFileRefList", this.fileUtil.getFileRefsByObjectId(id));
		return "training/advisor/viewAdvisorInfo";
	}
	
	/** 
	* @Title: viewUserInfo 
	* @Description: 查看导师信息 
	* @param  @param model
	* @param  @param request
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/training/setadvisor/nsm/opt-view/viewUserInfo.do"})
	public String viewUserInfo(ModelMap model,HttpServletRequest request) {
		String id = request.getParameter("id");
		OuterUserInfo userInfo = this.outerUserInfoService.getOuterUserInfoById(id);
		model.addAttribute("vo", userInfo);
		model.addAttribute("listFile", this.fileUtil.getFileRefsByObjectId(id));
		String flag = "true";
		if(!(this.fileUtil.getFileRefsByObjectId(id).size()>0)) {
			flag = "false";
		}
		String projectCode = userInfo.getParticipateProjectStr();
		String projectName = "";
		if(projectCode != null && projectCode.indexOf(",") > 0 && projectCode.split(",").length > 0) {
			for(String s : projectCode.split(",")) {
				projectName = projectName + this.dicUtil.getDicInfo("TRAINING_TEACHER_PROJECT", s).getName() + ",";
			}
		}
		model.addAttribute("flag", flag);
		model.addAttribute("projectName", DataUtil.isNotNull(projectName) ? projectName.substring(0, projectName.lastIndexOf(",")) : "");
		return "/training/common/outerUserInfoView";
	}
	
	/** 
	* @Title: queryOrderAdvisorPage 
	* @Description: 学生预约列表页
	* @param  @param model
	* @param  @param request
	* @param  @param advisorApply
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/training/orderadvisor/opt-query/queryOrderAdvisorPage.do"})
	public String queryOrderAdvisorPage(ModelMap model,HttpServletRequest request,
			AdvisorApplyInfo advisorApply) {
		String pageNo=request.getParameter("pageNo") !=null? request.getParameter("pageNo"):"1";
		Dic advisorStatus = this.dicUtil.getDicInfo("ADVISOR_STATUS", "PUBLISHED");
		String userId = this.sessionUtil.getCurrentUserId();
		boolean flag = ProjectSessionUtils.checkIsStudent(request);//判断当前登录人是不是学生
		advisorApply.setStudentId(new StudentInfoModel());
		advisorApply.getStudentId().setId(userId);
		if(advisorApply.getAdvisorInfoId() == null) {
			advisorApply.setAdvisorInfoId(new AdvisorInfo());
		}
		advisorApply.getAdvisorInfoId().setAdvisorStatus(advisorStatus);
		List<Dic> applyStatusList = this.dicUtil.getDicInfoList("STUDENT_ADVISOR_APPLY_STATUS");
		List<Dic> schoolYearList = this.dicUtil.getDicInfoList("YEAR");
		Page page = this.advisorApplyService.queryAdvisorApplyPage(advisorApply, Integer.parseInt(pageNo), Page.DEFAULT_PAGE_SIZE);
		model.addAttribute("page", page);
		model.addAttribute("flag", flag);
		model.addAttribute("applyStatusList", applyStatusList);
		model.addAttribute("schoolYearList", schoolYearList);
		model.addAttribute("advisorApply", advisorApply);
		return "training/advisor/queryAdvisorApplyList";
	}
	
	/** 
	* @Title: saveAdvisorApply 
	* @Description: 学生预约
	* @param  @param model
	* @param  @param request
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/training/orderadvisor/opt-save/saveAdvisorApply.do"})
	public String saveAdvisorApply(ModelMap model,HttpServletRequest request) {
		String id = request.getParameter("id");
		String returnSource = "redirect:/training/orderadvisor/opt-query/queryOrderAdvisorPage.do";
		AdvisorInfo advisorInfo = this.advisorApplyService.getAdvisorById(id);
		AdvisorApplyInfo advisorApply = new AdvisorApplyInfo();
		Dic applyStatus = this.dicUtil.getDicInfo("STUDENT_ADVISOR_APPLY_STATUS", "APPLYED");
		StudentInfoModel stu = this.studentCommonService.queryStudentById(this.sessionUtil.getCurrentUserId());
		if(stu == null) {
			returnSource = "/training/common/errorMessage";
		}
		advisorApply.setAdvisorInfoId(advisorInfo);
		advisorApply.setApplyStatus(applyStatus);
		advisorApply.setStudentId(stu);
		this.advisorApplyService.saveAdvisorApply(advisorApply);
		advisorInfo.setAdvisorStuNum(advisorInfo.getAdvisorStuNum()+1);
		this.advisorApplyService.updateAdvisorInfo(advisorInfo);
		return returnSource;
	}
	
	/**
	 * 取消预约信息
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping({"/training/orderadvisor/opt-save/saveAdvisorApplyCancel.do"})
	public String cancelApplyInfo(ModelMap model, HttpServletRequest request) {
		String id = request.getParameter("id");
		AdvisorApplyInfo advisorApplyInfo = this.advisorApplyService.getAdvisorApplyById(id);
		AdvisorInfo advisorInfo = this.advisorApplyService.getAdvisorById(advisorApplyInfo.getAdvisorInfoId().getId());
		Integer stuNum = advisorInfo.getAdvisorStuNum();
		if(stuNum > 0) {
			stuNum = stuNum - 1;
			advisorInfo.setAdvisorStuNum(stuNum);
			this.advisorApplyService.updateAdvisorInfo(advisorInfo);
		}
		this.advisorApplyService.deleteAdvisorApply(advisorApplyInfo);
		return "redirect:/training/orderadvisor/opt-query/queryOrderAdvisorPage.do";
	}
}
