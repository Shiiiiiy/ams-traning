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
import org.springframework.web.bind.annotation.ResponseBody;

import com.uws.core.base.BaseController;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.util.DataUtil;
import com.uws.domain.training.OuterUserInfo;
import com.uws.domain.training.SeminarManage;
import com.uws.domain.training.SeminarSubscribe;
import com.uws.sys.model.Dic;
import com.uws.sys.model.UploadFileRef;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.FileUtil;
import com.uws.sys.service.impl.DicFactory;
import com.uws.sys.service.impl.FileFactory;
import com.uws.training.service.IOuterUserInfoService;
import com.uws.training.service.ISeminarService;
import com.uws.training.util.TrainingConstants;

/**
 * @className AdvisorApplyController.java
 * @package com.uws.training.controller
 * @description
 * @author 联合永道
 * @date 2015年10月16日 16:04:06
 */
@Controller
public class SeminarManageController extends BaseController {
	@Autowired
	private ISeminarService seminarService;
	@Autowired
	private IOuterUserInfoService outerUserInfoService;
	@Autowired
	private static DicUtil dicUtil = DicFactory.getDicUtil();
	
	private FileUtil fileUtil = FileFactory.getFileUtil();
	
	/**
	 * 讲座信息列表
	 * @param model
	 * @param request
	 * @param po
	 * @return
	 */
	@RequestMapping({TrainingConstants.TRAINING_SEMINAR + "/opt-query/querySeminarInfoPage"})
	public String seminarInfoList(ModelMap model, HttpServletRequest request, SeminarManage po) {
		List<Dic> seminarStatus = dicUtil.getDicInfoList("SEMINAR_STATUS");
		model.addAttribute("seminarStatus", seminarStatus);
		String pageNo = request.getParameter("pageNo");
		pageNo = pageNo != null ? pageNo : "1";
		Page page = seminarService.queryPageSeminarInfo(po,Integer.parseInt(pageNo));
		model.addAttribute("page", page);
		model.addAttribute("po", po);
		return TrainingConstants.TRAINING_SEMINAR +"/seminarInfoList";
	}
	
	/**
	 * 新增或者更新讲座信息
	 * @param model
	 * @param request
	 * @param po
	 * @return
	 */
	@RequestMapping({TrainingConstants.TRAINING_SEMINAR + "/opt-add/seminarInfo", TrainingConstants.TRAINING_SEMINAR + "/opt-update/seminarInfo"})
	public String seminarInfo(ModelMap model, HttpServletRequest request, SeminarManage po) {

		List<OuterUserInfo> outerUserList = this.outerUserInfoService.getAllOuterUserList();
		model.addAttribute("outerUserList", outerUserList);
		SeminarManage vo = null;
		if(DataUtil.isNotNull(po.getId())) {
			model.addAttribute("uploadFileRefList", this.fileUtil.getFileRefsByObjectId(po.getId()));
			vo = seminarService.getSeminarInfoById(po.getId());
			model.addAttribute("vo", vo);
		}
		return TrainingConstants.TRAINING_SEMINAR + "/seminarInfoEdit";
	}
	
	/**
	 * 通过讲师id获得讲师的编号
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value ={"/training/chooseTeacher/opt-query/queryOuterUserInfoJson"},produces = {"text/plain;charset=UTF-8" })
	@ResponseBody
	public String checkClassInfo(ModelMap model, HttpServletRequest request) {
		String outerUserId = request.getParameter("id");
		String userCode = "";
		OuterUserInfo outerUserInfo = this.outerUserInfoService.getOuterUserInfoById(outerUserId);
		if(DataUtil.isNotNull(outerUserInfo)) {
			userCode = outerUserInfo.getUserCode();
		}
		return userCode;
	}
	
	/**
	 * 存储讲座信息
	 * @param model
	 * @param request
	 * @param po
	 * @return
	 */
	@RequestMapping({TrainingConstants.TRAINING_SEMINAR + "/opt-save/seminarInfo"})
	public String saveSeminarInfo(ModelMap model, HttpServletRequest request, SeminarManage po, String[] fileId) {
		String status = request.getParameter("status_id");
		//0保存，1提交
		if(status.equals("0")) {
			po.setSeminarStatus(dicUtil.getDicInfo("SEMINAR_STATUS", "SAVED"));
		}else {
			po.setSeminarStatus(dicUtil.getDicInfo("SEMINAR_STATUS", "SUBMITED"));
		}
		String seminarDateStr = request.getParameter("semDate");
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date seminarDate;
		try {
			seminarDate= format.parse(seminarDateStr.toString());
			po.setSeminarDate(seminarDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if(DataUtil.isNull(po.getId())) {
			seminarService.saveSeminarInfo(po, fileId);
		}else {
			seminarService.updateSeminarInfo(po, fileId);
		}
		return "redirect:" + TrainingConstants.TRAINING_SEMINAR + "/opt-query/querySeminarInfoPage.do";
	}
	/**
	 * 查看讲座信息
	 * @param model
	 * @param request
	 * @param po
	 * @return
	 */
	@RequestMapping({"/training/seminar/opt-view/seminarInfo"})
	public String viewSeminarInfo(ModelMap model, HttpServletRequest request, SeminarManage po) {
		SeminarManage seminarManage = this.seminarService.getSeminarInfoById(po.getId());
		model.addAttribute("seminarManage", seminarManage);
		model.addAttribute("uploadFileRefList", this.fileUtil.getFileRefsByObjectId(po.getId()));
		return TrainingConstants.TRAINING_SEMINAR + "/seminarInfoView";
	}
	
	/**
	 * 删除讲座信息
	 * @param model
	 * @param request
	 * @param po
	 * @return
	 */
	@RequestMapping({TrainingConstants.TRAINING_SEMINAR + "/opt-del/delSeminarInfo"})
	@ResponseBody
	public String delSeminarInfo(ModelMap model, HttpServletRequest request, SeminarManage po){
		this.seminarService.delSeminarInfo(po.getId());
		return "success";
	}
	
	/**
	 * 讲座发布信息编辑及确认
	 * @param model
	 * @param request
	 * @param seminarId
	 * @return
	 */
	@RequestMapping({TrainingConstants.TRAINING_SEMINAR + "/opt-pub/pubSeminarInfoEdit"})
	public String publishSeminarInfoEdit(ModelMap model, HttpServletRequest request, SeminarSubscribe po) {
		//获取当前附件的id字符串
		List<UploadFileRef> uploadFileRefList = this.fileUtil.getFileRefsByObjectId(po.getId());
		String fileList = new String();
		for(UploadFileRef f:uploadFileRefList){
			fileList += f.getId()+",";
		}
		model.addAttribute("fileList", fileList);
		SeminarManage seminarManage = seminarService.getSeminarInfoById(po.getId());
		model.addAttribute("seminarManage", seminarManage);
		//通过讲座的id获取学院在该讲座上的预约信息
		String pageNo = request.getParameter("pageNo");
		pageNo = pageNo != null ? pageNo : "1";
		Page confirmPage = seminarService.queryPageSeminar(po,Integer.parseInt(pageNo));
		model.addAttribute("confirmPage", confirmPage);
		model.addAttribute("uploadFileRefList", uploadFileRefList);
		return TrainingConstants.TRAINING_SEMINAR + "/seminarinfoConfirm";
	}
	
	
	/**
	 * 发布讲座信息保存
	 * @param model
	 * @param request
	 * @param po
	 * @return
	 */
	@RequestMapping({TrainingConstants.TRAINING_SEMINAR + "/opt-pub/pubSeminarInfo"})
	@ResponseBody
	public String pubSeminarInfo(ModelMap model, HttpServletRequest request, String seminarId,
			String seminarDateStr, String appointPlace, String attendNum, String fileList, String beginDate, String endDate){
		//附件id串
		String[] fileId = null;
		//通过seminarId获取该讲座的基本信息
		SeminarManage seminarManage = seminarService.getSeminarInfoById(seminarId);
		//将该讲座的状态更改为已发布
		Dic publish = dicUtil.getDicInfo("SEMINAR_STATUS", "PUBLISH");
		seminarManage.setSeminarStatus(publish);
		if(!seminarDateStr.equals(seminarManage.getSeminarDate())) {
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Date seminarDate;
			try {
				seminarDate = format.parse(seminarDateStr);
				seminarManage.setSeminarDate(seminarDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		if(!appointPlace.equals(seminarManage.getAppointPlace())) {
			seminarManage.setAppointPlace(appointPlace);
		}
//		if(!attendNum.equals(seminarManage.getAttendNum())) {
//			seminarManage.setAttendNum(Integer.parseInt(attendNum));
//		}
		if(!beginDate.equals(seminarManage.getBeginDate())) {
			seminarManage.setBeginDate(beginDate);
		}
		if(!endDate.equals(seminarManage.getEndDate())) {
			seminarManage.setEndDate(endDate);
		}
		seminarService.updateSeminarInfo(seminarManage, fileId);
		return "success";
	}
}