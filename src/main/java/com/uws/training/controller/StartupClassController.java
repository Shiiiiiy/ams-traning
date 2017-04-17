/**   
* @Title: StartupClassController.java 
* @Package com.uws.training.controller 
* @Description: (用一句话描述该文件做什么) 
* @author zhangyb   
* @date 2015年10月21日 下午1:28:53 
* @version V1.0   
*/
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uws.common.service.IBaseDataService;
import com.uws.common.util.SchoolYearUtil;
import com.uws.comp.service.ICompService;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.session.SessionFactory;
import com.uws.core.session.SessionUtil;
import com.uws.core.util.DataUtil;
import com.uws.domain.base.BaseAcademyModel;
import com.uws.domain.base.BaseClassModel;
import com.uws.domain.base.BaseMajorModel;
import com.uws.domain.base.BaseTeacherModel;
import com.uws.domain.training.StartupClassApplyInfo;
import com.uws.domain.training.StartupClassInfo;
import com.uws.sys.model.Dic;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.FileUtil;
import com.uws.sys.service.impl.DicFactory;
import com.uws.sys.service.impl.FileFactory;
import com.uws.training.service.IStartupClassService;
import com.uws.training.util.TrainingConstants;
import com.uws.user.model.User;

/** 
 * @ClassName: StartupClassController 
 * @Description: 创业班维护，结业管理
 * @author zhangyb 
 * @date 2015年10月21日 下午1:28:53  
 */
@Controller
public class StartupClassController {

	@Autowired
	private DicUtil dicUtil = DicFactory.getDicUtil();
	private SessionUtil sessionUtil = SessionFactory.getSession(null);
	@Autowired
	private IStartupClassService startupClassService;
	@Autowired
	private IBaseDataService baseDataService;
	private FileUtil fileUtil=FileFactory.getFileUtil();
	@Autowired
	private ICompService compService;
	
	/** 
	* @Title: queryStartupClassPage 
	* @Description:  创业班维护列表页
	* @param  @param model
	* @param  @param request
	* @param  @param startupClassInfo
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/training/startupclass{teacherType}/opt-query/queryStartupClassPage.do"})
	public String queryStartupClassPage(ModelMap model,HttpServletRequest request,
		StartupClassInfo startupClassInfo, @PathVariable String teacherType) {
		String userId = sessionUtil.getCurrentUserId();
		BaseTeacherModel btm = this.baseDataService.findTeacherById(userId);
		if(DataUtil.isNotNull(btm)) {
			startupClassInfo.setGrowthClassLeader(btm);
		}
		String pageNo=request.getParameter("pageNo") !=null? request.getParameter("pageNo"):"1";
		Dic saveStatusDic = this.dicUtil.getDicInfo("STARTUP_CLASS_STATUS", "SAVED");
		List<Dic> classTypeList = this.dicUtil.getDicInfoList("STARTUP_CLASS_TYPE");
		List<Dic> classStatusList = this.dicUtil.getDicInfoList("STARTUP_CLASS_STATUS");
		List<Dic> schoolYearList = this.dicUtil.getDicInfoList("YEAR");
		if(teacherType.equals("AO")) {   	//创业班创建 保留已保存 班级状态
			if(!classStatusList.contains(saveStatusDic)) {
				classStatusList.add(0,saveStatusDic);
			}
		}else if(teacherType.equals("HM")){ //创业班维护 去掉已保存 班级状态
			if(classStatusList.contains(saveStatusDic)) {
				classStatusList.remove(saveStatusDic);   
			}
		}
		Page page = this.startupClassService.queryStartupClassPage(startupClassInfo, Integer.parseInt(pageNo), Page.DEFAULT_PAGE_SIZE, teacherType);
		model.addAttribute("page", page);
		model.addAttribute("classInfo", startupClassInfo);
		model.addAttribute("classTypeList", classTypeList);
		model.addAttribute("classStatusList", classStatusList);
		model.addAttribute("schoolYearList", schoolYearList);
		model.addAttribute("teacherType", teacherType);
		return "training/startupClass/queryStartupClassList";
	}
	
	/** 
	* @Title: editStartupClass 
	* @Description:  新增或修改创业班信息
	* @param  @param model
	* @param  @param request
	* @param  @param startupClassInfo
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/training/startupclass/opt-edit/editStartupClass.do"})
	public String editStartupClass(ModelMap model,HttpServletRequest request,
			StartupClassInfo startupClassInfo) {
		String id = request.getParameter("id");
		if(DataUtil.isNotNull(id)) {
			startupClassInfo = this.startupClassService.getStartupClassById(id);
			startupClassInfo.setGrowthClassLeaderStr(startupClassInfo.getGrowthClassLeader().getName());
			model.addAttribute("uploadFileRefList", this.fileUtil.getFileRefsByObjectId(id));
		}
		List<Dic> classTypeList = this.dicUtil.getDicInfoList("STARTUP_CLASS_TYPE");
		model.addAttribute("classInfo", startupClassInfo);
		model.addAttribute("classTypeList", classTypeList);
		return "training/startupClass/editStartupClassInfo";
	}
	
	/** 
	* @Title: saveStartupClass 
	* @Description:  保存创业班信息
	* @param  @param model
	* @param  @param request
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/training/startupclass/opt-edit/saveStartupClass.do"})
	public String saveStartupClass(ModelMap model,HttpServletRequest request,
			StartupClassInfo startupClassInfo,String[] fileId) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date applyDate = sdf.parse(startupClassInfo.getApplyEndDateStr());
			Date beginDate = sdf.parse(startupClassInfo.getClassBeginDateStr());
			Date endDate = sdf.parse(startupClassInfo.getPlanEndDateStr());
			startupClassInfo.setApplyEndDate(applyDate);
			startupClassInfo.setClassBeginDate(beginDate);
			startupClassInfo.setPlanEndDate(endDate);
		} catch (ParseException e) {
			//  Auto-generated catch block
			e.printStackTrace();
		}
		BaseTeacherModel newTeacher = this.baseDataService.findTeacherById(startupClassInfo.getGrowthClassLeader().getId());
		startupClassInfo.setGrowthClassLeader(newTeacher);
		Dic classStatus = this.dicUtil.getDicInfo("STARTUP_CLASS_STATUS", "SAVED");
		Dic year = SchoolYearUtil.getYearDic();
		startupClassInfo.setSchoolYear(year);
		startupClassInfo.setGrowthClassType(this.dicUtil.getDicInfo("STARTUP_CLASS_TYPE", startupClassInfo.getGrowthClassType().getCode()));
		startupClassInfo.setGrowthClassStatus(classStatus);
		if(DataUtil.isNotNull(startupClassInfo.getId())) { 
			//更新保存
			User user = new User();
			user.setId(sessionUtil.getCurrentUserId());
			StartupClassInfo cla = this.startupClassService.getStartupClassById(startupClassInfo.getId());
			cla.setUpdator(user);
//			更新所选用户角色信息
			BaseTeacherModel oldTeacher = cla.getGrowthClassLeader();
			if(!((oldTeacher.getId()).equals(newTeacher.getId()))) {
				//如果被替换的老师只带了一个创业班，则将该老师的角色删除
				List<StartupClassInfo>  sci = this.startupClassService.getStartupClassByLeaderId(oldTeacher.getId());
				if(sci.size()==1) {
					this.startupClassService.updateUserRole(cla.getGrowthClassLeader().getId(), newTeacher.getId(), 
							TrainingConstants.STARTUP_CLASS_HEADEMASTER);
				}else {
					boolean flag = this.startupClassService.checkUserIsExist(newTeacher.getId(), TrainingConstants.STARTUP_CLASS_HEADEMASTER);
					if(!flag){
						this.startupClassService.saveUserRole(newTeacher.getId(), TrainingConstants.STARTUP_CLASS_HEADEMASTER);
					}
				}
			}
			BeanUtils.copyProperties(startupClassInfo, cla, new String[]{"id","createTime","updateTime","creator"});
			this.startupClassService.updateStartupClass(cla, fileId);
		}else{
			//新增保存
			User user = new User();
			user.setId(sessionUtil.getCurrentUserId());
			startupClassInfo.setCreator(user);
//			保存所选用户角色信息
			boolean flag = this.startupClassService.checkUserIsExist(newTeacher.getId(), TrainingConstants.STARTUP_CLASS_HEADEMASTER);
			if(!flag) {
				this.startupClassService.saveUserRole(newTeacher.getId(), TrainingConstants.STARTUP_CLASS_HEADEMASTER);
			}
			this.startupClassService.saveStartupClass(startupClassInfo, fileId);
		}
		return "redirect:/training/startupclassAO/opt-query/queryStartupClassPage.do";
	}
	
	/** 
	* @Title: editClassCourse 
	* @Description: 创业班提交后班主任可以修改课程附件 
	* @param  @param model
	* @param  @param request
	* @param  @param startupClassInfo
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/training/startupclass/opt-edit/editClassCourse.do"})
	public String editClassCourse(ModelMap model,HttpServletRequest request,
			StartupClassInfo startupClassInfo) {
		String id = request.getParameter("id");
		if(DataUtil.isNotNull(id)) {
			startupClassInfo = this.startupClassService.getStartupClassById(id);
			startupClassInfo.setGrowthClassLeaderStr(startupClassInfo.getGrowthClassLeader().getName());
			model.addAttribute("uploadFileRefList", this.fileUtil.getFileRefsByObjectId(id));
		}
		List<Dic> classTypeList = this.dicUtil.getDicInfoList("STARTUP_CLASS_TYPE");
		model.addAttribute("classInfo", startupClassInfo);
		model.addAttribute("classTypeList", classTypeList);
		return "training/startupClass/editStartupClassCourse";
	}
	
	/** 
	* @Title: saveStartupClassCourse 
	* @Description: 保存班主任上传的附件
	* @param  @param model
	* @param  @param request
	* @param  @param startupClassInfo
	* @param  @param fileId
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/training/startupclass/opt-save/saveStartupClassCourse.do"})
	public String saveStartupClassCourse(ModelMap model,HttpServletRequest request,
			String[] fileId) {
		String classId = request.getParameter("id");
		this.startupClassService.updateClassFile(classId, fileId);
		return "redirect:/training/startupclassHM/opt-query/queryStartupClassPage.do";
	}
	
	/** 
	* @Title: submitStartupClass 
	* @Description:  提交创业班信息
	* @param  @param model
	* @param  @param request
	* @param  @param startupClassInfo
	* @param  @param fileId
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/training/startupclass/opt-edit/submitStartupClass.do"})
	public String submitStartupClass(ModelMap model,HttpServletRequest request,
			StartupClassInfo startupClassInfo,String[] fileId) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date applyDate = sdf.parse(startupClassInfo.getApplyEndDateStr());
			Date beginDate = sdf.parse(startupClassInfo.getClassBeginDateStr());
			Date endDate = sdf.parse(startupClassInfo.getPlanEndDateStr());
			startupClassInfo.setApplyEndDate(applyDate);
			startupClassInfo.setClassBeginDate(beginDate);
			startupClassInfo.setPlanEndDate(endDate);
		} catch (ParseException e) {
			//  Auto-generated catch block
			e.printStackTrace();
		}
		BaseTeacherModel newTeacher = this.baseDataService.findTeacherById(startupClassInfo.getGrowthClassLeader().getId());
		startupClassInfo.setGrowthClassLeader(newTeacher);
		Dic classStatus = this.dicUtil.getDicInfo("STARTUP_CLASS_STATUS", "SUBMITTED");
		Dic year = SchoolYearUtil.getYearDic();
		startupClassInfo.setSchoolYear(year);
		startupClassInfo.setGrowthClassStatus(classStatus);
		startupClassInfo.setGrowthClassType(this.dicUtil.getDicInfo("STARTUP_CLASS_TYPE", startupClassInfo.getGrowthClassType().getCode()));
		if(DataUtil.isNotNull(startupClassInfo.getId())) {
			//保存更新
			StartupClassInfo cla = this.startupClassService.getStartupClassById(startupClassInfo.getId());
//			更新所选用户角色信息
			BaseTeacherModel oldTeacher = cla.getGrowthClassLeader();
			if(!(oldTeacher.getId().equals(newTeacher.getId()))) {
				//如果被替换的老师只带了一个创业班，则将该老师的角色删除
				List<StartupClassInfo> sci = this.startupClassService.getStartupClassByLeaderId(oldTeacher.getId());
				if(sci.size()==1) {
					this.startupClassService.updateUserRole(cla.getGrowthClassLeader().getId(), newTeacher.getId(), 
							TrainingConstants.STARTUP_CLASS_HEADEMASTER);
				}else {
					boolean flag = this.startupClassService.checkUserIsExist(newTeacher.getId(), TrainingConstants.STARTUP_CLASS_HEADEMASTER);
					if(!flag){
						this.startupClassService.saveUserRole(newTeacher.getId(), TrainingConstants.STARTUP_CLASS_HEADEMASTER);
					}
				}
			}
			BeanUtils.copyProperties(startupClassInfo, cla, new String[]{"id","createTime","updateTime","creator"});
			this.startupClassService.updateStartupClass(cla, fileId);
		}else{
			User user = new User();
			user.setId(sessionUtil.getCurrentUserId());
			startupClassInfo.setCreator(user);
//			保存所选用户角色信息
			boolean flag = this.startupClassService.checkUserIsExist(newTeacher.getId(), TrainingConstants.STARTUP_CLASS_HEADEMASTER);
			if(!flag) {
				this.startupClassService.saveUserRole(newTeacher.getId(), TrainingConstants.STARTUP_CLASS_HEADEMASTER);
			}
			this.startupClassService.saveStartupClass(startupClassInfo, fileId);
		}
		return "redirect:/training/startupclassAO/opt-query/queryStartupClassPage.do";
	}
	
	/**
	 * 查看创业班信息
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping({"/training/startupclass/opt-view/viewStartupClass.do"})
	public String viewStartupClass(ModelMap model,HttpServletRequest request) {
		String id = request.getParameter("id");
		StartupClassInfo classInfo = this.startupClassService.getStartupClassById(id);
		model.addAttribute("classInfo", classInfo);
		model.addAttribute("uploadFileRefList", this.fileUtil.getFileRefsByObjectId(id));
		return "training/startupClass/viewStartupClassInfo";
	}
	/** 
	* @Title: delStartupClass 
	* @Description:  删除创业班信息
	* @param  @param model
	* @param  @param request
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/training/startupclass/opt-del/delStartupClass.do"})
	public String delStartupClass(ModelMap model,HttpServletRequest request) {
		String id = request.getParameter("id");
		StartupClassInfo classInfo = this.startupClassService.getStartupClassById(id);
//		同时删除用户角色
		//判断该老师是不是只带了一个创业班
		List<StartupClassInfo> sci = this.startupClassService.getStartupClassByLeaderId(classInfo.getGrowthClassLeader().getId());
		if(sci.size()==1) {
			this.startupClassService.deleteUserRole(classInfo.getGrowthClassLeader().getId(), TrainingConstants.STARTUP_CLASS_HEADEMASTER);
		}
		this.startupClassService.deleteStartupClass(classInfo);
		return "redirect:/training/startupclassAO/opt-query/queryStartupClassPage.do";
	}
	
	/** 
	* @Title: checkStartupClass 
	* @Description:  验证创业班名称是否重复
	* @param  @param growthClassName
	* @param  @param id
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/training/startupclass/opt-query/checkStartupClass.do"})
	@ResponseBody
	public String checkStartupClass(@RequestParam String growthClassName,@RequestParam String id) {
		String result = "true";
		boolean flag = DataUtil.isNotNull(id);
		List<StartupClassInfo> classInfoList = this.startupClassService.getClassInfoListByName(growthClassName);
		if(flag) {
			StartupClassInfo classInfo = this.startupClassService.getStartupClassById(id);
			if(classInfoList.size() > 0 && !classInfo.getGrowthClassName().equals(growthClassName)) {
				result = "false";
			}
		}else{
			if(classInfoList.size() > 0) {
				result = "false";
			}
		}
		return result;
	}
	
	/** 
	* @Title: queryApproveStuPage 
	* @Description:  创业班维护查看学生及调整学生
	* @param  @param model
	* @param  @param request
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/training/startupclass/opt-query/queryApproveStuPage.do"})
	public String queryApproveStuPage(ModelMap model,HttpServletRequest request,StartupClassApplyInfo applyInfo) {
		String classId = request.getParameter("id");
		if(DataUtil.isNotNull(classId)) {
			StartupClassInfo classInfo = this.startupClassService.getStartupClassById(classId);
			model.addAttribute("classInfo", classInfo);
		}else{
			StartupClassInfo classInfo = this.startupClassService.getStartupClassById(applyInfo.getGrowthId().getId());
			model.addAttribute("classInfo", classInfo);
		}
//		StartupClassApplyInfo applyInfo = new StartupClassApplyInfo();
		if(applyInfo.getGrowthId() == null) {
			applyInfo.setGrowthId(new StartupClassInfo());
			applyInfo.getGrowthId().setId(classId);
		}
		String pageNo=request.getParameter("pageNo") !=null? request.getParameter("pageNo"):"1";
		List<BaseAcademyModel> academyList = this.baseDataService.listBaseAcademy();
		// 下拉列表 专业
		List<BaseMajorModel> majorList =null;
		if (null != applyInfo && null != applyInfo.getStudentId() 
				&& null != applyInfo.getStudentId().getCollege() 
				&& null != applyInfo.getStudentId().getCollege().getId()
				&& applyInfo.getStudentId().getCollege().getId().length() > 0) {
			majorList = compService.queryMajorByCollage(applyInfo.getStudentId().getCollege().getId());
		}
		// 下拉列表 班级
		List<BaseClassModel> classList =null;
		if (null != applyInfo && null != applyInfo.getStudentId() 
				&& null != applyInfo.getStudentId().getClassId() 
				&& null != applyInfo.getStudentId().getMajor() 
				&& null != applyInfo.getStudentId().getMajor().getId() 
				&& applyInfo.getStudentId().getMajor().getId().length() > 0) {
			classList = compService.queryClassByMajor(applyInfo.getStudentId().getMajor().getId());
		}
		//set审核状态为审核通过
		Dic approveStatus = dicUtil.getDicInfo("STARTUP_APPROVE_STATUS", "APPROVED");
		applyInfo.setApproveStatus(approveStatus);
		Page page = this.startupClassService.queryStuApprovePage(applyInfo, Integer.parseInt(pageNo), Page.DEFAULT_PAGE_SIZE, null);
		model.addAttribute("page", page);
		model.addAttribute("applyInfo", applyInfo);
		model.addAttribute("academyList", academyList);
		model.addAttribute("majorList", majorList);
		model.addAttribute("classList", classList);
		return "training/startupClass/queryAdjustStuList";
	}
	
	/** 
	* @Title: saveStuApprove 
	* @Description:  保存单个学生调整
	* @param  @param model
	* @param  @param request
	* @param  @param applyInfo
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/training/startupclass/opt-save/saveStuApprove.do"})
	public String saveStuApprove(ModelMap model,HttpServletRequest request, StartupClassApplyInfo applyInfo) {
		Dic applyStatus = this.dicUtil.getDicInfo("STARTUP_APPLY_STATUS", "APPLYED");
		Dic approveStatus = this.dicUtil.getDicInfo("STARTUP_APPROVE_STATUS", "APPROVED");
		Dic completeStatus = this.dicUtil.getDicInfo("STARTUP_COMPLETE_STATUS", "UNCOMPLETE");
		StartupClassApplyInfo oldApplyInfo = this.startupClassService.getStuApplyInfoById(applyInfo.getId());
		String growthClassId = applyInfo.getGrowthId().getId();
		StartupClassInfo classInfo = this.startupClassService.getStartupClassById(growthClassId);
		oldApplyInfo.setGrowthId(classInfo);
		oldApplyInfo.setApplyStatus(applyStatus);
		oldApplyInfo.setApproveStatus(approveStatus);
		oldApplyInfo.setCompleteStatus(completeStatus);
		this.startupClassService.updateStuApplyInfo(oldApplyInfo);
		return "redirect:/training/startupclass/opt-query/queryApproveStuPage.do?id=" + growthClassId;
	}
	
	/** 
	* @Title: startupClass 
	* @Description:  开班操作
	* @param  @param model
	* @param  @param request
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/training/startupclass/opt-update/startupClass.do"})
	public String startupClass(ModelMap model,HttpServletRequest request) {
		String id = request.getParameter("id");
		StartupClassInfo classInfo = this.startupClassService.getStartupClassById(id);
		Dic classStatus = this.dicUtil.getDicInfo("STARTUP_CLASS_STATUS", "OPENING");
		classInfo.setGrowthClassStatus(classStatus);
		this.startupClassService.updateStartupClass(classInfo);
		return "redirect:/training/startupclassHM/opt-query/queryStartupClassPage.do";
	}
	
	/** 
	* @Title: endupClass 
	* @Description: 结班 
	* @param  @param model
	* @param  @param request
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/training/startupclass/opt-update/endupClass.do"})
	public String endupClass(ModelMap model,HttpServletRequest request) {
		String id = request.getParameter("id");
		StartupClassInfo classInfo = this.startupClassService.getStartupClassById(id);
		Dic classStatus = this.dicUtil.getDicInfo("STARTUP_CLASS_STATUS", "CLOSED");
		classInfo.setGrowthClassStatus(classStatus);
		this.startupClassService.updateStartupClass(classInfo);
		return "redirect:/training/startupclassHM/opt-query/queryStartupClassPage.do";
	}
	/** 
	* @Title: checkStartupClass 
	* @Description: 验证是否能开班 
	* @param  @param request
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/training/startupclass/opt-update/checkStartupClass.do"})
	@ResponseBody
	public String checkStartupClass(HttpServletRequest request) {
		String startupClassId = request.getParameter("id");
		String flag = new String();
		List<StartupClassApplyInfo> classApplyInfo = this.startupClassService.getStuApplyInfoByClassId(startupClassId);
		if(classApplyInfo.size()>0) {
			String approvedId = dicUtil.getDicInfo("STARTUP_APPROVE_STATUS", "APPROVED").getId();
			String unApproveId = this.dicUtil.getDicInfo("STARTUP_APPROVE_STATUS", "UNAPPROVE").getId();
			String rejectId = this.dicUtil.getDicInfo("STARTUP_APPROVE_STATUS", "REJECT").getId();
			for(StartupClassApplyInfo classId :classApplyInfo) {
				if(classId.getApproveStatus().getId().equals(approvedId)) {
					flag += "approve,";
				}else if(classId.getApproveStatus().getId().equals(unApproveId)) {
					flag += "unapprove,";
				}else if(classId.getApproveStatus().getId().equals(rejectId)) {
					flag += "reject,";
				}
			}
			if(!flag.contains("approve")) {
				flag = "3";  //没有审核通过的学生
			}else if(flag.contains("unapprove")) {
				//有未审核的学生
				flag = "1";
			}else if(!flag.contains("unapprove")) {
				//报名的学生全部审核完了
				flag = "2";
			}
		}else {
			//没有学生报名
			flag = "0";
		}
		
		//		boolean flag = this.startupClassService.checkClassCanOpen(id);
		return flag+"";
	}
	
	/** 
	* @Title: queryCompleteClassPage 
	* @Description:  结业管理列表页
	* @param  @param model
	* @param  @param request
	* @param  @param applyInfo
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/training/completeclass/opt-query/queryCompleteStuPage.do"})
	public String queryCompleteClassPage(ModelMap model,HttpServletRequest request,StartupClassApplyInfo applyInfo) {
		String pageNo=request.getParameter("pageNo") !=null? request.getParameter("pageNo"):"1";
		List<BaseAcademyModel> academyList = this.baseDataService.listBaseAcademy();
		// 下拉列表 专业
		List<BaseMajorModel> majorList =null;
		if (null != applyInfo && null != applyInfo.getStudentId() 
				&& null != applyInfo.getStudentId().getCollege() 
				&& null != applyInfo.getStudentId().getCollege().getId()
				&& applyInfo.getStudentId().getCollege().getId().length() > 0) {
			majorList = compService.queryMajorByCollage(applyInfo.getStudentId().getCollege().getId());
		}
		// 下拉列表 班级
		List<BaseClassModel> classList =null;
		if (null != applyInfo && null != applyInfo.getStudentId() 
				&& null != applyInfo.getStudentId().getClassId() 
				&& null != applyInfo.getStudentId().getMajor() 
				&& null != applyInfo.getStudentId().getMajor().getId() 
				&& applyInfo.getStudentId().getMajor().getId().length() > 0) {
			classList = compService.queryClassByMajor(applyInfo.getStudentId().getMajor().getId());
		}
		List<Dic> classTypeList = this.dicUtil.getDicInfoList("STARTUP_CLASS_TYPE");
		List<Dic> schoolYearList = this.dicUtil.getDicInfoList("YEAR");
		List<Dic> completeList = this.dicUtil.getDicInfoList("STARTUP_COMPLETE_STATUS");
		Dic applyStatus = this.dicUtil.getDicInfo("STARTUP_APPLY_STATUS", "APPLYED");
		Dic approveStatus = this.dicUtil.getDicInfo("STARTUP_APPROVE_STATUS", "APPROVED");
		Dic classStatus = this.dicUtil.getDicInfo("STARTUP_CLASS_STATUS", "CLOSED");
		applyInfo.setApproveStatus(approveStatus);
		applyInfo.setApplyStatus(applyStatus);
		if(applyInfo.getGrowthId() == null) {
			applyInfo.setGrowthId(new StartupClassInfo());
		}
		applyInfo.getGrowthId().setGrowthClassStatus(classStatus);
		String userId = sessionUtil.getCurrentUserId();
		Page page = this.startupClassService.queryStuApprovePage(applyInfo, Integer.parseInt(pageNo), Page.DEFAULT_PAGE_SIZE, userId);
		model.addAttribute("page", page);
		model.addAttribute("applyInfo", applyInfo);
		model.addAttribute("schoolYearList", schoolYearList);
		model.addAttribute("completeList", completeList);
		model.addAttribute("classTypeList", classTypeList);
		model.addAttribute("academyList", academyList);
		model.addAttribute("majorList", majorList);
		model.addAttribute("classList", classList);
		return "training/startupClass/queryCompleteStuList";
	}
	
	/** 
	* @Title: editStuComplete 
	* @Description:  单条记录结业
	* @param  @param model
	* @param  @param request
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/training/completeclass/opt-update/editStuComplete.do"})
	public String editStuComplete(ModelMap model,HttpServletRequest request) {
		String id = request.getParameter("id");
		StartupClassApplyInfo applyInfo = this.startupClassService.getStuApplyInfoById(id);
		Dic completeStatus = this.dicUtil.getDicInfo("STARTUP_COMPLETE_STATUS", "COMPLETED");
		applyInfo.setCompleteStatus(completeStatus);
		this.startupClassService.updateStuApplyInfo(applyInfo);
		return "redirect:/training/completeclass/opt-query/queryCompleteStuPage.do";
	}
	
	/** 
	* @Title: editMulStuComplete 
	* @Description:  批量结业学生记录
	* @param  @param model
	* @param  @param request
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/training/completeclass/opt-update/editMulStuComplete.do"})
	public String editMulStuComplete(ModelMap model,HttpServletRequest request) {
		String[] applyInfoArr = request.getParameterValues("stuApplyIds");
		Dic completeStatus = this.dicUtil.getDicInfo("STARTUP_COMPLETE_STATUS", "COMPLETED");
		if(applyInfoArr.length > 0) {
			for(String s : applyInfoArr) {
				StartupClassApplyInfo applyInfo = this.startupClassService.getStuApplyInfoById(s);
				applyInfo.setCompleteStatus(completeStatus);
				this.startupClassService.updateStuApplyInfo(applyInfo);
			}
		}
		return "redirect:/training/completeclass/opt-query/queryCompleteStuPage.do";
	}
	
}
