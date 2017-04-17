package com.uws.training.controller;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.uws.common.service.IBaseDataService;
import com.uws.common.service.IStudentCommonService;
import com.uws.core.base.BaseController;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.session.SessionFactory;
import com.uws.core.session.SessionUtil;
import com.uws.core.util.DataUtil;
import com.uws.domain.base.BaseAcademyModel;
import com.uws.domain.base.BaseTeacherModel;
import com.uws.domain.training.SeminarApply;
import com.uws.domain.training.SeminarManage;
import com.uws.domain.training.SeminarSubscribe;
import com.uws.domain.training.SeminarSubscribeList;
import com.uws.sys.model.Dic;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.FileUtil;
import com.uws.sys.service.IDicService;
import com.uws.sys.service.impl.DicFactory;
import com.uws.sys.service.impl.FileFactory;
import com.uws.training.util.TrainingConstants;
import com.uws.training.service.ISeminarService;
import com.uws.util.ProjectSessionUtils;
/**
 * @className SeminarApplyController.java
 * @package com.uws.training.controller
 * @description
 * @author 联合永道
 * @date 2015年10月15日 17:56:43
 *
 */
@Controller
public class SeminarApplyController extends BaseController {
	@Autowired
	private IDicService dicService;
	@Autowired
	private ISeminarService seminarService;
	@Autowired
	private IBaseDataService baseDataService;
	@Autowired
	private static DicUtil dicUtil = DicFactory.getDicUtil();
	@Autowired
	private IStudentCommonService studentCommonService;
	private SessionUtil sessionUtil = SessionFactory.getSession(TrainingConstants.TRAINING_SEMINAR);
	private FileUtil fileUtil = FileFactory.getFileUtil();
	
	/**
	 * 学院预约信息列表
	 * @param model
	 * @param request
	 * @param po
	 * @return
	 */
	@RequestMapping({TrainingConstants.TRAINING_SUBSCRIBE + "/opt-query/querySubscribeInfoPage"})
	public String seminarSubInfoList(ModelMap model, HttpServletRequest request, SeminarSubscribeList po) {
		//当前登录人所在的学院
		String userId = this.sessionUtil.getCurrentUserId();
		BaseTeacherModel teacherModel = this.baseDataService.findTeacherById(userId);
		if(DataUtil.isNotNull(teacherModel)) {
			BaseAcademyModel collegeModel = baseDataService.findAcademyById(teacherModel.getOrg().getId());
			if(DataUtil.isNotNull(collegeModel)) {
				po.setCollegeId(collegeModel);
				boolean flag = true;
				model.addAttribute("flag", flag);
			}
		}
		//预约状态
		List<Dic> appointStatus = dicService.getDicInfoList("COLLEGE_APPOINT_STATUS");
		model.addAttribute("appointStatus", appointStatus);
		String pageNo = request.getParameter("pageNo");
		pageNo = pageNo != null ? pageNo : "1";
		Page page = seminarService.queryPageSeminarSubscribeInfo(po, Integer.parseInt(pageNo));
		model.addAttribute("page", page);
		model.addAttribute("po", po);
		return TrainingConstants.TRAINING_SEMINAR + "/seminarSubscribeList";
	}
	
	/**
	 * 学院预约信息或者学生报名信息查看
	 * @param model
	 * @param request
	 * @param po
	 * @return
	 */
	@RequestMapping({TrainingConstants.TRAINING_SUBSCRIBE + "/nsm/seminarSubscribeInfo", TrainingConstants.TRAINING_APPLY + "/nsm/viewSeminarApplyInfo"})
	public String seminarSubInfoView(ModelMap model, HttpServletRequest request, String seminarId, String collegeId, String studentId) {
		if(DataUtil.isNotNull(collegeId)) {
			SeminarSubscribe subscribeInfo = seminarService.getSeminarSubscribeBySeminarIdAndCollegeId(seminarId, collegeId);
			model.addAttribute("subscribeInfo", subscribeInfo);
		}
		if(DataUtil.isNotNull(studentId)) {
			SeminarApply applyInfo = seminarService.getSeminarApplyInfoBySeminarIdAndStudentId(seminarId, studentId);
			model.addAttribute("applyInfo", applyInfo);
		}
		SeminarManage seminarManage = seminarService.getSeminarInfoById(seminarId);
		model.addAttribute("uploadFileRefList", this.fileUtil.getFileRefsByObjectId(seminarId));
		model.addAttribute("seminarManage", seminarManage);
		if(DataUtil.isNotNull(collegeId)) {
			return TrainingConstants.TRAINING_SEMINAR + "/seminarSubInfoView";
		}else {
			return TrainingConstants.TRAINING_SEMINAR + "/seminarApplyInfoView";
		}
	}
	
	/**
	 * 学院预约信息或学生报名信息编辑
	 * @param model
	 * @param request
	 * @param seminarId
	 * @param flag
	 * @return
	 */
	@RequestMapping({TrainingConstants.TRAINING_SUBSCRIBE + "/nsm/seminarSubscribeInfoEdit", TrainingConstants.TRAINING_APPLY + "/nsm/seminarApplyInfoEdit"})
	public String seminarSubscribeEdit(ModelMap model, HttpServletRequest request, String seminarId, String flag) {
		if(DataUtil.isNotNull(seminarId)) {
			SeminarManage seminarManage = seminarService.getSeminarInfoById(seminarId);
			model.addAttribute("seminarManage", seminarManage);
		}
		String userId = this.sessionUtil.getCurrentUserId();
		BaseTeacherModel teacherModel = this.baseDataService.findTeacherById(userId);
		SeminarSubscribe subscribeInfo = new SeminarSubscribe();
		String collegeId = (String)request.getSession().getAttribute("_teacher_orgId");
		BaseAcademyModel baseCollege = baseDataService.findAcademyById(collegeId);
		subscribeInfo.setAppointAcademy(baseCollege);
		subscribeInfo.setAppointUserId(teacherModel);
		model.addAttribute("uploadFileRefList", this.fileUtil.getFileRefsByObjectId(seminarId));
		model.addAttribute("subscribeInfo", subscribeInfo);
		if(flag.equals("apply")) {
			return TrainingConstants.TRAINING_SEMINAR + "/seminarApplyInfoView";
		}else {
			return TrainingConstants.TRAINING_SEMINAR + "/seminarSubscribeInfo";
		}
	}
	
	/**
	 * 学院预约信息保存
	 * @param model
	 * @param request
	 * @param po
	 * @return
	 */
	@RequestMapping({TrainingConstants.TRAINING_SUBSCRIBE + "/opt-sub/subscribeInfo.do"})
	public String subscribeInfo(ModelMap model, HttpServletRequest request, String seminarId, 
			String appointPlace, Integer appointNum, String appointTime, String beginDate, String endDate,
			String appointPhone,String appointEmail,String appointQQ) {
		SeminarSubscribe subscribeInfo = new SeminarSubscribe();
		String userId = sessionUtil.getCurrentUserId();
		BaseTeacherModel appointUser = baseDataService.findTeacherById(userId);
		subscribeInfo.setAppointUserId(appointUser);
		subscribeInfo.setAppointStatus(dicUtil.getDicInfo("COLLEGE_APPOINT_STATUS", "APPOINTED"));
		subscribeInfo.setSeminarId(this.seminarService.getSeminarInfoById(seminarId));
		subscribeInfo.setAppointPlace(appointPlace);
		subscribeInfo.setAppointPhone(appointPhone);
		subscribeInfo.setAppointEmail(appointEmail);
		subscribeInfo.setAppointQQ(appointQQ);
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date d;
		try {
			d= format.parse(appointTime.toString());
			subscribeInfo.setAppointTime(d);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		subscribeInfo.setAppointNum(appointNum);
		subscribeInfo.setBeginDate(beginDate);
		subscribeInfo.setEndDate(endDate);
		subscribeInfo.setAppointAcademy(baseDataService.findAcademyById(appointUser.getOrg().getId()));
		this.seminarService.saveSeminarSubInfo(subscribeInfo);
		return "redirect:" + TrainingConstants.TRAINING_SUBSCRIBE + "/opt-query/querySubscribeInfoPage.do";
	}
	/**
	 * 学生报名信息列表
	 * @param model
	 * @param request
	 * @param po
	 * @param vo
	 * @return
	 */
	@RequestMapping({TrainingConstants.TRAINING_APPLY + "/opt-query/querySeminarApplyPage"})
	public String seminarApplyInfoList(ModelMap model, HttpServletRequest request, SeminarApply po) {
		//判断当前登录人是学生
		boolean flag = ProjectSessionUtils.checkIsStudent(request);
		model.addAttribute("flag", flag);
		if(flag==true) {
			po.setStudentId(studentCommonService.queryStudentById(sessionUtil.getCurrentUserId()));
		}
		//报名状态
		List<Dic> applyStatus = dicService.getDicInfoList("STU_APPLY_STATUS");
		model.addAttribute("applyStatus", applyStatus);
		String pageNo = request.getParameter("pageNo");
		pageNo = pageNo != null ? pageNo : "1";
		Page page = seminarService.queryPageSeminarApplyInfo(po, Integer.parseInt(pageNo));
		model.addAttribute("page", page);
		model.addAttribute("po", po);
		return TrainingConstants.TRAINING_SEMINAR + "/seminarApplyList";
	}
	
	/**
	 * 学生报名信息保存
	 * @param model
	 * @param request
	 * @param po
	 * @return
	 */
	@RequestMapping({TrainingConstants.TRAINING_APPLY + "/opt-apply/seminarApplyInfo"})
	public String seminarApply(ModelMap model, HttpServletRequest request, String seminarId) {
		SeminarApply applyInfo = new SeminarApply();
		String userId = sessionUtil.getCurrentUserId();
		applyInfo.setSeminarId(this.seminarService.getSeminarInfoById(seminarId));
		applyInfo.setStudentId(studentCommonService.queryStudentById(userId));
		applyInfo.setApplyStatus(dicUtil.getDicInfo("STU_APPLY_STATUS", "APPLIED"));
		seminarService.saveSeminarApplyInfo(applyInfo);
		return "redirect:" + TrainingConstants.TRAINING_APPLY + "/opt-query/querySeminarApplyPage.do";
	}
}