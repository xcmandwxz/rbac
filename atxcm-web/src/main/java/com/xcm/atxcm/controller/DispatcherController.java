package com.xcm.atxcm.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;

import com.xcm.atxcm.bean.AJAXResult;
import com.xcm.atxcm.bean.Permission;
import com.xcm.atxcm.bean.User;
import com.xcm.atxcm.service.PermissionService;
import com.xcm.atxcm.service.UserService;

@Controller
public class DispatcherController {

	@Autowired
	private UserService userService;
	
	
	@Autowired
	private PermissionService permissionService;
	
	@RequestMapping("/login")
	public String login() {
		
		return "login";
	}
	
	//�û�ע��
	@RequestMapping("/logout")
	public String logout(HttpSession session) {
		//session.removeAttribute("loginUser");
		session.invalidate();
		return "redirect:login";
	}
	
	//ͨ��ajax�ύ��½
	@RequestMapping("/doAJAXLogin")
	public Object doAJAXLogin(User user ,HttpSession session) {
		AJAXResult result = new AJAXResult();
		User dbUser = userService.query4Login(user);
		if(dbUser != null ){
			session.setAttribute("loginUser", dbUser);
			//��ȡ�û�Ȩ����Ϣ
			//�����û���ȡ����Ȩ����Ϣ
			List<Permission> permissions = permissionService.queryPermissionsByUser(dbUser);
			Map<Integer,Permission> permissionMap = new HashMap<Integer, Permission>();
			Permission root = null;
			//��Ȩ��ѭ������װ��map��   ��Ȩ��id��Ȩ�ޣ�
			for(Permission permission : permissions) {
				permissionMap.put(permission.getId(),permission);
			}
			for (Permission permission : permissions) {
				Permission child = permission;
				if(child.getPid() == 0) {
					root = permission;
				}else {
					Permission parent = permissionMap.get(child.getPid());
					parent.getChildren().add(child);
				}
			}
			session.setAttribute("rootPermission", root);
			
			
			result.setSuccess(true);
		}else {
			result.setSuccess(false);
		}
		
		return result;
	}
	
	
	@RequestMapping("/doLogin")
	public String doLogin( User user, Model model ) throws Exception {
		
		
		//��spring�ṩ�Ĺ����������������
		// �������ַ������մ���ı��뷽ʽת��Ϊԭʼ���ֽ�������
		//byte[] bs = loginacct.getBytes("ISO8859-1");
		
		// ��ԭʼ���ֽ������а�����ȷ�ı���ת��Ϊ��ȷ�����ּ��ɡ�
		//loginacct = new String(bs, "UTF-8");
		
		
		// 1) ��ȡ������
		// 1-1) HttpServletRequest
		// 1-2) �ڷ��������б������ӱ���Ӧ�Ĳ�����������ͬ
		// 1-3) ���ǽ������ݷ�װΪʵ�������
		
		// 2) ��ѯ�û���Ϣ
		User dbUser = userService.query4Login(user);
		
		// 3) �ж��û���Ϣ�Ƿ����
		if ( dbUser != null ) {
			// ��½�ɹ�����ת����ҳ��
			return "main";
		} else {
			// ��½ʧ�ܣ���ת�ص���½ҳ�棬��ʾ������Ϣ
			String errorMsg = "��½�˺Ż����벻��ȷ������������";
			model.addAttribute("errorMsg", errorMsg);
			return "redirect:login";
		}
		
		
	}
}
