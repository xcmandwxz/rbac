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
	
	//用户注销
	@RequestMapping("/logout")
	public String logout(HttpSession session) {
		//session.removeAttribute("loginUser");
		session.invalidate();
		return "redirect:login";
	}
	
	//通过ajax提交登陆
	@RequestMapping("/doAJAXLogin")
	public Object doAJAXLogin(User user ,HttpSession session) {
		AJAXResult result = new AJAXResult();
		User dbUser = userService.query4Login(user);
		if(dbUser != null ){
			session.setAttribute("loginUser", dbUser);
			//获取用户权限信息
			//根据用户获取它的权限信息
			List<Permission> permissions = permissionService.queryPermissionsByUser(dbUser);
			Map<Integer,Permission> permissionMap = new HashMap<Integer, Permission>();
			Permission root = null;
			//将权限循环出来装进map里   （权限id，权限）
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
		
		
		//用spring提供的过滤器解决编码问题
		// 将乱码字符串按照错误的编码方式转换为原始的字节码序列
		//byte[] bs = loginacct.getBytes("ISO8859-1");
		
		// 将原始的字节码序列按照正确的编码转换为正确的文字即可。
		//loginacct = new String(bs, "UTF-8");
		
		
		// 1) 获取表单数据
		// 1-1) HttpServletRequest
		// 1-2) 在方法参数列表中增加表单对应的参数，名称相同
		// 1-3) 就是将表单数据封装为实体类对象
		
		// 2) 查询用户信息
		User dbUser = userService.query4Login(user);
		
		// 3) 判断用户信息是否存在
		if ( dbUser != null ) {
			// 登陆成功，跳转到主页面
			return "main";
		} else {
			// 登陆失败，跳转回到登陆页面，提示错误信息
			String errorMsg = "登陆账号或密码不正确，请重新输入";
			model.addAttribute("errorMsg", errorMsg);
			return "redirect:login";
		}
		
		
	}
}
