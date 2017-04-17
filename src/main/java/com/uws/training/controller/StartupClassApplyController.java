/**   
* @Title: StartupClassApplyController.java 
* @Package com.uws.training.controller 
* @Description: (用一句话描述该文件做什么) 
* @author zhangyb   
* @date 2015年10月21日 下午1:29:39 
* @version V1.0   
*/
package com.uws.training.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uws.common.service.IBaseDataService;
import com.uws.common.service.IStudentCommonService;
import com.uws.common.util.SchoolYearUtil;
import com.uws.comp.service.ICompService;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.session.SessionFactory;
import com.uws.core.session.SessionUtil;
import com.uws.core.util.DataUtil;
import com.uws.domain.base.BaseAcademyModel;
import com.uws.domain.base.BaseClassModel;
import com.uws.domain.base.BaseMajorModel;
import com.uws.domain.orientation.StudentInfoModel;
import com.uws.domain.training.StartupClassApplyInfo;
import com.uws.domain.training.StartupClassInfo;
import com.uws.sys.model.Dic;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.FileUtil;
import com.uws.sys.service.impl.DicFactory;
import com.uws.sys.service.impl.FileFactory;
import com.uws.training.service.IStartupClassService;
import com.uws.training.util.TrainingConstants;
import com.uws.util.ProjectSessionUtils;

/** 
 * @ClassName: StartupClassApplyController 
 * @Description:  学生报名、报名调整功能
 * @author zhangyb 
 * @date 2015年10月21日 下午1:29:39  
 */
@Controller
public class StartupClassApplyController {
	
	@Autowired
	private DicUtil dicUtil = DicFactory.getDicUtil();
	private SessionUtil sessionUtil = SessionFactory.getSession(null);
	@Autowired
	private IStartupClassService startupClassService;
	@Autowired
	private IBaseDataService baseDataService;
	@Autowired
	private IStudentCommonService studentCommonService;
	@Autowired
	private ICompService compService;
	private FileUtil fileUtil=FileFactory.getFileUtil();
	
	/** 
	* @Title: querySubClassPage 
	* @Description:  学生报名列表页
	* @param  @param model
	* @param  @param request
	* @param  @param classInfo
	* @param  @param applyInfo
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/training/applyclass/opt-query/querySubClassPage.do"})
	public String querySubClassPage(ModelMap model,HttpServletRequest request,
			StartupClassInfo classInfo, StartupClassApplyInfo applyInfo) {
		String pageNo=request.getParameter("pageNo") !=null? request.getParameter("pageNo"):"1";
		String userId = this.sessionUtil.getCurrentUserId();
		boolean flag = ProjectSessionUtils.checkIsStudent(request);   //判断当前登录人是不是学生
		List<Dic> classTypeList = this.dicUtil.getDicInfoList("STARTUP_CLASS_TYPE");
		List<Dic> applyStatusList = this.dicUtil.getDicInfoList("STARTUP_APPLY_STATUS");
		List<Dic> approveStatusList = this.dicUtil.getDicInfoList("STARTUP_APPROVE_STATUS");
		Dic statusDic = this.dicUtil.getDicInfo("STARTUP_CLASS_STATUS", "SUBMITTED");
		Dic curYear = SchoolYearUtil.getYearDic();
		classInfo.setSchoolYear(curYear);
		classInfo.setGrowthClassStatus(statusDic);
		Page page = this.startupClassService.queryStuApplyPage(classInfo, Integer.parseInt(pageNo), 
				Page.DEFAULT_PAGE_SIZE,userId,applyInfo.getApplyStatus(),applyInfo.getApproveStatus());
		model.addAttribute("classTypeList", classTypeList);
		model.addAttribute("applyStatusList", applyStatusList);
		model.addAttribute("approveStatusList", approveStatusList);
		model.addAttribute("classInfo", classInfo);
		model.addAttribute("applyInfo", applyInfo);
		model.addAttribute("page", page);
		model.addAttribute("flag", flag);
		return "training/startupClass/queryStuApplyList";
	}
	/** 
	* @Title: editStuApply 
	* @Description:  学生报名
	* @param  @param model
	* @param  @param request
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/training/applyclass/opt-edit/editStuApply.do"})
	public String editStuApply(ModelMap model,HttpServletRequest request) {
		//班级id
		String classInfoId = request.getParameter("id");
		//审核状态
		String auditStatus = request.getParameter("auditStatus");
		Dic applyStatus = this.dicUtil.getDicInfo("STARTUP_APPLY_STATUS", "APPLYED");
		Dic approveStatus = this.dicUtil.getDicInfo("STARTUP_APPROVE_STATUS", "UNAPPROVE");
		Dic completeStatus = this.dicUtil.getDicInfo("STARTUP_COMPLETE_STATUS", "UNCOMPLETE");
		StartupClassInfo classInfo = this.startupClassService.getStartupClassById(classInfoId);
		boolean flag = ProjectSessionUtils.checkIsStudent(request);   //判断当前登录人是不是学生
		String userId = sessionUtil.getCurrentUserId();
		if(!flag) {
			return "/training/common/errorMessage";
		}else{
			//判断是审核拒绝状态下的还是新增的
			if("true".equals(auditStatus)) {
				StartupClassApplyInfo applyInfo = this.startupClassService.getStartupClassApplyInfoBystuIdAndClassId(userId, classInfoId);
				applyInfo.setApproveStatus(approveStatus);
				applyInfo.setComments("审核拒绝");
				this.startupClassService.updateStuApplyInfo(applyInfo);
			}else {
				StudentInfoModel stu = this.studentCommonService.queryStudentById(userId);
				StartupClassApplyInfo applyInfo = new StartupClassApplyInfo();
				applyInfo.setApplyStatus(applyStatus);
				applyInfo.setApproveStatus(approveStatus);;
				applyInfo.setGrowthId(classInfo);
				applyInfo.setStudentId(stu);
				applyInfo.setCompleteStatus(completeStatus);
				this.startupClassService.saveStuApplyInfo(applyInfo);	
			}
			return "redirect:/training/applyclass/opt-query/querySubClassPage.do";
		}
	}
	
	/**
	 * 取消报名
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping({"/training/applyclass/opt-edit/cancelStuApply.do"})
	public String cancelStuApply(ModelMap model, HttpServletRequest request) {
		String id = request.getParameter("id");
		StartupClassApplyInfo po = this.startupClassService.getStuApplyInfoById(id);
		if("审核拒绝".equals(po.getComments())) {
			Dic applyStatus = dicUtil.getDicInfo("STARTUP_APPLY_STATUS", "APPLYED");
			Dic approveStatus = dicUtil.getDicInfo("STARTUP_APPROVE_STATUS", "REJECT");
			//将该条信息的报名状态设为已报名，审核状态设置为审核拒绝
			po.setApplyStatus(applyStatus);
			po.setApproveStatus(approveStatus);
			this.startupClassService.updateStuApplyInfo(po);
		}else {
			this.startupClassService.delStuApplyInfo(po);
		}
		return "redirect:/training/applyclass/opt-query/querySubClassPage.do";
	}
	
	/**
	 * 我的创业班信息
	 * @param model
	 * @param request
	 * @param po
	 * @return
	 */
	@RequestMapping({"training/startupClassInfo/opt-query/queryMyStartupClassPage.do"})
	public String queryMyStartClassInfo(ModelMap model, HttpServletRequest request, StartupClassApplyInfo po) {
		//创业班类型
		List<Dic> classTypeList = this.dicUtil.getDicInfoList("STARTUP_CLASS_TYPE");
		model.addAttribute("classTypeList", classTypeList);
		List<Dic> completeStatusList = this.dicUtil.getDicInfoList("STARTUP_COMPLETE_STATUS");
		model.addAttribute("completeStatusList", completeStatusList);
		//学生id作为过滤条件
		String userId = this.sessionUtil.getCurrentUserId();
		StudentInfoModel studentInfo = this.studentCommonService.queryStudentById(userId);
		po.setStudentId(studentInfo);
		String pageNo=request.getParameter("pageNo") !=null? request.getParameter("pageNo"):"1";
		Page page = this.startupClassService.queryMyStartClassInfoPage(po, Integer.parseInt(pageNo), Page.DEFAULT_PAGE_SIZE);
		model.addAttribute("page", page);
		model.addAttribute("po", po);
		return "training/startupClass/queryMyStartupClassInfoList";
	}
	
	
	
	
	/** 
	* @Title: queryStuApprovePage 
	* @Description:  学生报名审核列表
	* @param  @param model
	* @param  @param request
	* @param  @param applyInfo
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/training/approveclass/opt-query/queryStuApprovePage.do"})
	public String queryStuApprovePage(ModelMap model,HttpServletRequest request, StartupClassApplyInfo applyInfo) {
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
		List<Dic> approveStatusList = this.dicUtil.getDicInfoList("STARTUP_APPROVE_STATUS");
		String userId = this.sessionUtil.getCurrentUserId();
		if(("system").equalsIgnoreCase(sessionUtil.getCurrentLoginName())) {
			userId=null;
		}
		Page page = this.startupClassService.queryStuApprovePage(applyInfo, Integer.parseInt(pageNo), Page.DEFAULT_PAGE_SIZE, userId);
		model.addAttribute("page", page);
		model.addAttribute("applyInfo", applyInfo);
		model.addAttribute("schoolYearList", schoolYearList);
		model.addAttribute("classTypeList", classTypeList);
		model.addAttribute("approveStatusList", approveStatusList);
		model.addAttribute("academyList", academyList);
		model.addAttribute("majorList", majorList);
		model.addAttribute("classList", classList);
		model.addAttribute("userId", userId);
		return "training/startupClass/queryStuApproveList";
	}
	
	/** 
	* @Title: editStuApprove 
	* @Description:  单个调整学生申请
	* @param  @param model
	* @param  @param request
	* @param  @param applyInfo
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/training/approveclass/opt-edit/editStuApprove.do"})
	public String editStuApprove(ModelMap model,HttpServletRequest request, StartupClassApplyInfo applyInfo) {
		String id = request.getParameter("id");
//		判断是否返回创业班维护列表页
		String type = request.getParameter("type");
		applyInfo = this.startupClassService.getStuApplyInfoById(id);
		Dic classStatusDic = this.dicUtil.getDicInfo("STARTUP_CLASS_STATUS", "SAVED");
		String userId;
		if("set".equals(type)) {
			userId = null;
		}else {
			userId = sessionUtil.getCurrentUserId();
		}
//		查询状态不是已保存的创业班
		List<StartupClassInfo> classInfoList = this.startupClassService.getSubClassInfoList(classStatusDic, userId);
		model.addAttribute("applyInfo", applyInfo);
		model.addAttribute("type", type);
		model.addAttribute("classInfoList", classInfoList);
		model.addAttribute("uploadFileRefList", this.fileUtil.getFileRefsByObjectId(applyInfo.getGrowthId().getId()));
		return "training/startupClass/editStuApply";
	}
	
	/** 
	* @Title: changeGrowthClass 
	* @Description: 切换班级后刷新页面 
	* @param  @param model
	* @param  @param request
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/training/changeclass/nsm/changeGrowthClass.do"})
	public String changeGrowthClass(ModelMap model,HttpServletRequest request) {
		
		String id = request.getParameter("id");;
		StartupClassApplyInfo applyInfo = this.startupClassService.getStuApplyInfoById(id);
		String oldClassId = applyInfo.getGrowthId().getId();
		Dic classStatusDic = this.dicUtil.getDicInfo("STARTUP_CLASS_STATUS", "SAVED");
		String classId = request.getParameter("classId");
		StartupClassInfo classInfo = this.startupClassService.getStartupClassById(classId);
		applyInfo.setGrowthId(classInfo);
		List<StartupClassInfo> classInfoList = null;
//		判断是否为招就办管理员
		boolean roleFlag = this.startupClassService.checkUserIsExist(sessionUtil.getCurrentUserId(), TrainingConstants.STARTUP_TRAINING_ADMIN);
		if(roleFlag){
			classInfoList = this.startupClassService.getSubClassInfoList(classStatusDic, null);
		}else{
			classInfoList = this.startupClassService.getSubClassInfoList(classStatusDic, sessionUtil.getCurrentUserId());
		}
		model.addAttribute("applyInfo", applyInfo);
		model.addAttribute("oldClassId", oldClassId);
		model.addAttribute("classInfoList", classInfoList);
		model.addAttribute("uploadFileRefList", this.fileUtil.getFileRefsByObjectId(applyInfo.getGrowthId().getId()));
		return "training/startupClass/growthClassInfoInclude";
	}
	
	/**
	 * 单个调整时检验所调班级该学生是否已经报名
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value ={"/training/checkClass/opt-query/checkClassInfoJson", "/training/checkMultyApply/opt-query/checkMultyApplyInfo"},produces = { "text/plain;charset=UTF-8" })
	@ResponseBody
	public String checkClassInfo(ModelMap model, HttpServletRequest request) {
		//创业班id
		String classId = request.getParameter("classId");
		String applyId = request.getParameter("applyId");
		StartupClassInfo classInfo = this.startupClassService.getStartupClassById(classId);
		//单个调整学生id
		String stuNumber = request.getParameter("stuNumber");
		//批量调整学生id串
		String stuIds = request.getParameter("stuIds");
		//已报名的学生名字串
		String names = "";
		String flag = "flase";
		if(DataUtil.isNotNull(stuNumber)) {
			List<StartupClassApplyInfo> stuClassList = startupClassService.getStuApplyClassListByStuId(stuNumber);
			if(stuClassList.size()>0 && DataUtil.isNotNull(classInfo)) {
				for (StartupClassApplyInfo po : stuClassList) {
					if(po.getGrowthId().getGrowthClassName().equals(classInfo.getGrowthClassName()) &&
							(!applyId.equals(po.getId()))) {
						flag = "true";
						break;
					}
				}
			}
		}else if(DataUtil.isNotNull(stuIds)) {
			String[] ids = stuIds.split(",");
			for(String stuId :ids) {
				StudentInfoModel sim = studentCommonService.queryStudentById(stuId);
				List<StartupClassApplyInfo> stuClassList = startupClassService.getStuApplyClassListByStuId(stuId);
				if(DataUtil.isNotNull(classInfo) && stuClassList.size()>0) {
					for(StartupClassApplyInfo po :stuClassList) {
						if(po.getGrowthId().getGrowthClassName().equals(classInfo.getGrowthClassName())) {
							flag = "true";
							break;
						}
					}
					if(flag.equals("true")) {
						names += sim.getName()+",";
					}
				}
			}
			
		}
		StringBuffer sb = new StringBuffer("{'flag':'" + flag + "','names':'" + names+ "'}");
		return sb.toString();
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
	@RequestMapping({"/training/approveclass/opt-save/saveStuApprove.do"})
	public String saveStuApprove(ModelMap model,HttpServletRequest request, StartupClassApplyInfo applyInfo) {
		String flag = request.getParameter("flag");
		String oldClassId = request.getParameter("oldClassId");
		StartupClassInfo oldClassInfo = this.startupClassService.getStartupClassById(oldClassId);
		Dic applyStatus = this.dicUtil.getDicInfo("STARTUP_APPLY_STATUS", "APPLYED");
		Dic approve = this.dicUtil.getDicInfo("STARTUP_APPROVE_STATUS", "APPROVED");
		Dic reject = this.dicUtil.getDicInfo("STARTUP_APPROVE_STATUS", "REJECT");
		Dic completeStatus = this.dicUtil.getDicInfo("STARTUP_COMPLETE_STATUS", "UNCOMPLETE");
		StartupClassApplyInfo oldApplyInfo = this.startupClassService.getStuApplyInfoById(applyInfo.getId());
		String newClassId = applyInfo.getGrowthId().getId();
		StartupClassInfo newClassInfo = this.startupClassService.getStartupClassById(newClassId);
//		判断是否为招就办管理员
		boolean roleFlag = this.startupClassService.checkUserIsExist(sessionUtil.getCurrentUserId(), TrainingConstants.STARTUP_TRAINING_ADMIN);
		if(!(oldClassId.equals(newClassId))) {
			String comments = "已将你从"+oldClassInfo.getGrowthClassName()+"调整到"+newClassInfo.getGrowthClassName();
			oldApplyInfo.setComments(comments);
		}
		String returnPath = "redirect:/training/approveclass/opt-query/queryStuApprovePage.do";
		if(roleFlag) {
			oldApplyInfo.setGrowthId(newClassInfo);
			returnPath = "redirect:/training/startupclass/opt-query/queryApproveStuPage.do?id="+oldClassId;
		}else{
			if(flag.equals("0")) {
				oldApplyInfo.setGrowthId(newClassInfo);
				oldApplyInfo.setApplyStatus(applyStatus);
				oldApplyInfo.setApproveStatus(approve);
				oldApplyInfo.setCompleteStatus(completeStatus);		
			}else {
				oldApplyInfo.setApproveStatus(reject);
			}
		}
		this.startupClassService.updateStuApplyInfo(oldApplyInfo);
		return returnPath;
	}
	
	/** 
	* @Title: viewStuApprove 
	* @Description:  报名审核查看
	* @param  @param model
	* @param  @param request
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/training/approveclass/opt-view/viewStuApprove.do"})
	public String viewStuApprove(ModelMap model,HttpServletRequest request) {
		String id = request.getParameter("id");
		StartupClassApplyInfo applyInfo = this.startupClassService.getStuApplyInfoById(id);
		model.addAttribute("applyInfo", applyInfo);
		model.addAttribute("uploadFileRefList", this.fileUtil.getFileRefsByObjectId(applyInfo.getGrowthId().getId()));
		return "training/startupClass/viewStuApprove";
	}
	
	/** 
	* @Title: queryClassInfoJson 
	* @Description:  返回创业班信息json串
	* @param  @param model
	* @param  @param request
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping(value ={"/training/approveclass/opt-query/queryClassInfoJson.do"},produces = { "text/plain;charset=UTF-8" })
	@ResponseBody
	public String queryClassInfoJson(ModelMap model,HttpServletRequest request) {
		
		String id = request.getParameter("id");
		StartupClassInfo classInfo = this.startupClassService.getStartupClassById(id);
		if(DataUtil.isNull(classInfo.getComments())) {
			classInfo.setComments("");
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		StringBuffer sb = new StringBuffer("{'growthClassType':'" + classInfo.getGrowthClassType().getName()
				+ "','growthClassTheme':'" + classInfo.getGrowthClassTheme()
				+ "','growthClassLeader':'" + classInfo.getGrowthClassLeader().getName()
				+ "','applyEndDate':'" + sdf.format(classInfo.getApplyEndDate())
				+ "','classBeginDate':'" + sdf.format(classInfo.getClassBeginDate())
				+ "','planEndDate':'" + sdf.format(classInfo.getPlanEndDate())
				+ "','comments':'" + classInfo.getComments()
				+ "','id':'" + classInfo.getId()
				+ "'}"
				);
		return sb.toString();
	}
	
	/** 
	* @Title: editMultyAdjust 
	* @Description:  跳转到批量调整页面
	* @param  @param model
	* @param  @param request
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/training/approveclass/opt-edit/editMultyAdjust.do"})
	public String editMultyAdjust(ModelMap model,HttpServletRequest request) {
		String[] applyIdArr = request.getParameterValues("stuApplyIds");
		List<StartupClassApplyInfo> applyInfoList = new ArrayList<StartupClassApplyInfo>();
		//批量调整的学生的id串
		String stuIds = "";
		String applyInfoIds = "";
		if(applyIdArr.length > 0) {
			for(String s : applyIdArr) {
				StartupClassApplyInfo applyInfo = this.startupClassService.getStuApplyInfoById(s);
				stuIds += applyInfo.getStudentId().getId()+",";
				applyInfoList.add(applyInfo);
				applyInfoIds = applyInfoIds + s + ",";
			}
		}
		Dic classStatusDic = this.dicUtil.getDicInfo("STARTUP_CLASS_STATUS", "SAVED");
		String userId = sessionUtil.getCurrentUserId();
		List<StartupClassInfo> classInfoList = this.startupClassService.getSubClassInfoList(classStatusDic, userId);
		model.addAttribute("classInfoList", classInfoList);
		model.addAttribute("applyInfoList", applyInfoList);
		model.addAttribute("applyInfo", applyInfoList.get(0));
		model.addAttribute("applyInfoIds", applyInfoIds);
		model.addAttribute("uploadFileRefList", this.fileUtil.getFileRefsByObjectId(applyInfoList.get(0).getGrowthId().getId()));
		model.addAttribute("stuIds", stuIds);
		return "training/startupClass/editMultyStuApply";
	}
	
	/** 
	* @Title: saveMultyStuApprove 
	* @Description:  保存批量调整
	* @param  @param model
	* @param  @param request
	* @param  @param applyInfo
	* @param  @return    
	* @return String    
	* @throws 
	*/
	@RequestMapping({"/training/approveclass/opt-save/saveMultyStuApprove.do"})
	public String saveMultyStuApprove(ModelMap model,HttpServletRequest request, StartupClassApplyInfo applyInfo) {
		String flag = request.getParameter("flag");
		String applyIds = request.getParameter("applyInfoIds");
		String oldClassId = request.getParameter("oldClassId");
		StartupClassInfo oldClassInfo = this.startupClassService.getStartupClassById(oldClassId);
		String newClassId = applyInfo.getGrowthId().getId();
		StartupClassInfo newClassInfo = this.startupClassService.getStartupClassById(newClassId);
		String comment = null;
		if(!(oldClassId.equals(newClassId))) {
			comment = "已将你从"+oldClassInfo.getGrowthClassName()+"调整到"+newClassInfo.getGrowthClassName();
		}
		Dic applyStatus = this.dicUtil.getDicInfo("STARTUP_APPLY_STATUS", "APPLYED");
		Dic approve = this.dicUtil.getDicInfo("STARTUP_APPROVE_STATUS", "APPROVED");
		Dic reject = this.dicUtil.getDicInfo("STARTUP_APPROVE_STATUS", "REJECT");
		Dic completeStatus = this.dicUtil.getDicInfo("STARTUP_COMPLETE_STATUS", "UNCOMPLETE");
		String[] applyIdArr = null;
		if(applyIds.indexOf(",") > -1) {
			applyIdArr = applyIds.split(",");
		}
		if(applyIdArr.length > 0) {
			for(String s : applyIdArr) {
				StartupClassApplyInfo apply = this.startupClassService.getStuApplyInfoById(s);
				if(DataUtil.isNotNull(comment)) {
					apply.setComments(comment);
				}
				if(flag.equals("0")) {
					apply.setGrowthId(newClassInfo);
					apply.setApplyStatus(applyStatus);
					apply.setApproveStatus(approve);
					apply.setCompleteStatus(completeStatus);
				}else {
					apply.setApproveStatus(reject);
				}
				this.startupClassService.updateStuApplyInfo(apply);
			}
		}
		return "redirect:/training/approveclass/opt-query/queryStuApprovePage.do";
	}
	
}
