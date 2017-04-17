package com.uws.training.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.uws.common.service.IBaseDataService;
import com.uws.comp.service.ICompService;
import com.uws.core.base.BaseController;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.session.SessionFactory;
import com.uws.core.session.SessionUtil;
import com.uws.core.util.DataUtil;
import com.uws.domain.base.BaseAcademyModel;
import com.uws.domain.base.BaseClassModel;
import com.uws.domain.base.BaseMajorModel;
import com.uws.domain.orientation.StudentInfoModel;
import com.uws.domain.training.AdvisorInfo;
import com.uws.domain.training.SeminarManage;
import com.uws.domain.training.StartupClassInfo;
import com.uws.domain.training.StatisticSeminarApply;
import com.uws.domain.training.StatisticStartupComplete;
import com.uws.sys.model.Dic;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.impl.DicFactory;
import com.uws.training.service.IAdvisorApplyService;
import com.uws.training.service.ISeminarService;
import com.uws.training.service.IStartupClassService;
import com.uws.training.service.ITrainingStatisticService;
import com.uws.training.util.TrainingConstants;
import com.uws.util.ProjectSessionUtils;

/**
 * @className TrainingStatisticController.java
 * @package com.uws.training.controller
 * @description 统计
 * @author 联合永道
 * @date 2015年10月15日 17:56:43
 */
@Controller
public class TrainingStatisticController extends BaseController {
	@Autowired
	private IBaseDataService baseDateService;
	@Autowired
	private ICompService compService;
	@Autowired
	private ITrainingStatisticService trainingStatisticService;
	@Autowired
	private DicUtil dicUtil = DicFactory.getDicUtil();
	@Autowired
	private ISeminarService seminarService;
	@Autowired
	private IStartupClassService startupClassService;
	@Autowired
	private IAdvisorApplyService advisorApplyService;
	private SessionUtil sessionUtil = SessionFactory.getSession(TrainingConstants.TRAINING_SEMINAR);
	
	/**
	 * 讲座报名信息统计列表
	 * @param model
	 * @param request
	 * @param po
	 * @return
	 */
	@RequestMapping({TrainingConstants.TRAINING_APPLY_STATISTIC + "/opt-query/querySeminarApplyStatisticPage" })
	public String collegeStatisticSeminarApply(ModelMap model, HttpServletRequest request, StatisticSeminarApply po) {
		String seminarDateStr = request.getParameter("seminarDateStr");
		model.addAttribute("seminarDateStr", seminarDateStr);
		po.setSeminarDateStr(seminarDateStr);
		String pageNo = request.getParameter("pageNo");
		pageNo = pageNo != null ? pageNo : "1";
		Page page = trainingStatisticService.querySeminarApplyStatisticPage(po,
				Integer.parseInt(pageNo), Page.DEFAULT_PAGE_SIZE);
		model.addAttribute("page", page);
		model.addAttribute("po", po);
		return TrainingConstants.TRAINING_STATISTIC + "/seminarApplyStatisticList";
	}
	
	/**
	 * 报名学生信息列表
	 * @param model
	 * @param request
	 * @param seminarId
	 * @param po
	 * @return
	 */
	@RequestMapping({TrainingConstants.TRAINING_APPLY + "/opt-view/viewStudentInfo"})
	public String applyStudentInfoView(ModelMap model, HttpServletRequest request, StudentInfoModel po, String seminarId, 
			String growthId, String flag, String advisorId) {
		if(DataUtil.isNotNull(seminarId)) {
			SeminarManage seminarManage = seminarService.getSeminarInfoById(seminarId);
			model.addAttribute("seminarManage", seminarManage);
			model.addAttribute("seminarId", seminarId);
		}
		if(DataUtil.isNotNull(growthId)) {
			StartupClassInfo classInfo = startupClassService.getStartupClassById(growthId);
			model.addAttribute("classInfo", classInfo);
			model.addAttribute("growthId", growthId);
			model.addAttribute("flag",flag);
		}
		if(DataUtil.isNotNull(advisorId)) {
			AdvisorInfo advisorInfo = advisorApplyService.getAdvisorById(advisorId);
			model.addAttribute("advisorInfo",advisorInfo);
			model.addAttribute("advisorId",advisorId);
		}
		//学院列表
		List<BaseAcademyModel> collegeList = baseDateService.listBaseAcademy();
		List<BaseMajorModel> majorList = null;
		//根据返回的学院查询专业
		if(DataUtil.isNotNull(po.getCollege()) && DataUtil.isNotNull(po.getCollege().getId()))
			majorList = compService.queryMajorByCollage(po.getCollege().getId());
		List<BaseClassModel> klassList = null;
		//根据返回的专业查询班级
		if(DataUtil.isNotNull(po.getMajor()) && DataUtil.isNotNull(po.getMajor().getId()))
			klassList = compService.queryClassByMajor(po.getMajor().getId());
		model.addAttribute("collegeList", collegeList);
		model.addAttribute("majorList", majorList);
		model.addAttribute("klassList", klassList);
		String pageNo = request.getParameter("pageNo");
		pageNo = pageNo != null ? pageNo : "1";
		Page page = trainingStatisticService.queryStuInfoPage(po, seminarId, growthId, flag, advisorId, Integer.parseInt(pageNo), Page.DEFAULT_PAGE_SIZE);
		model.addAttribute("page", page);
		model.addAttribute("po", po);
		return TrainingConstants.TRAINING_STATISTIC + "/applyStuInfoList";
	}
	
	/**
	 * 创业班报名统计
	 * @param model
	 * @param request
	 * @param po
	 * @return
	 */
	@RequestMapping({TrainingConstants.TRAINING_STARTUP_STATISTIC + "/opt-query/queryStartupClassStatisticPage"})
	public String startupClassApplyStatistic(ModelMap model, HttpServletRequest request, StatisticStartupComplete po) {
		List<Dic> classTypeList = this.dicUtil.getDicInfoList("STARTUP_CLASS_TYPE");
		model.addAttribute("classTypeList", classTypeList);
		List<Dic> schoolYearList = this.dicUtil.getDicInfoList("YEAR");
		model.addAttribute("schoolYearList", schoolYearList);
		String userId = sessionUtil.getCurrentUserId();
		boolean flag = this.startupClassService.checkUserIsExist(userId, TrainingConstants.STARTUP_CLASS_HEADEMASTER);
		model.addAttribute("flag", flag);
		if(flag==true) {
			po.setTeacherName(ProjectSessionUtils.getCurrentUserName(request));
		}
		String pageNo = request.getParameter("pageNo");
		pageNo = pageNo != null ? pageNo : "1";
		Page page = trainingStatisticService.queryStartupClassApplyStatisticPage(po, Integer.parseInt(pageNo), Page.DEFAULT_PAGE_SIZE);
		model.addAttribute("page", page);
		model.addAttribute("po", po);
		return TrainingConstants.TRAINING_STATISTIC + "/startupClassApplyStatisticList";
	}
	
	/**
	 * 学生签到统计（导师）
	 * @param model
	 * @param request
	 * @param po
	 * @return
	 */
	@RequestMapping({TrainingConstants.TRAINING_ADVISOR_STATISTIC + "/opt-query/queryAdvisorApplyStatisticPage"})
	public String advisorApplyStatistic(ModelMap model, HttpServletRequest request, AdvisorInfo po) {
		List<Dic> schoolYearList = this.dicUtil.getDicInfoList("YEAR");
		model.addAttribute("schoolYearList", schoolYearList);
		List<Dic> gender = this.dicUtil.getDicInfoList("GENDER");
		model.addAttribute("gender", gender);
		//值班日期
		String dutyDate = request.getParameter("dutyDate");
		model.addAttribute("dutyDate", dutyDate);
		po.setAdvisorDateStr(dutyDate);
		String pageNo = request.getParameter("pageNo");
		pageNo = pageNo != null ? pageNo : "1";
		Page page = trainingStatisticService.queryAdvisorApplyStatisticPage(po, Integer.parseInt(pageNo), Page.DEFAULT_PAGE_SIZE);
		model.addAttribute("page", page);
		model.addAttribute("po",po);
		return TrainingConstants.TRAINING_STATISTIC + "/advisorApplyStatisticList";
	}
	

}