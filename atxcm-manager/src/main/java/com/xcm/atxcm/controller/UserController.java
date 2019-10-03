package com.xcm.atxcm.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.impl.interceptor.RetryInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xcm.atxcm.bean.AJAXResult;
import com.xcm.atxcm.bean.Page;
import com.xcm.atxcm.bean.User;
import com.xcm.atxcm.bean.Role;
import com.xcm.atxcm.service.RoleService;
import com.xcm.atxcm.service.UserService;

@Controller
@RequestMapping("/user")
public class UserController {
		@Autowired
		private UserService userService;
		@Autowired
		private RoleService roleService;
	
	
		@RequestMapping("/add")
		public String add() {
			return "user/add";
		}
		
		
		@ResponseBody
		@RequestMapping("/update")
		public Object update( User user ) {
			AJAXResult result = new AJAXResult();//AJAXResult�Լ������һ���࣬��������״̬�������ݵ�
			
			try {
				
				userService.updateUser(user);
				result.setSuccess(true);
			} catch ( Exception e ) {
				e.printStackTrace();
				result.setSuccess(false);
			}
			
			return result;
		}
		
		//����༭ҳ��
		@RequestMapping("/edit")
		public String edit( Integer id, Model model ) {
			
			User user = userService.queryById(id);//�ҵ��û�
			model.addAttribute("user", user);//��Ҫ����ҳ���û���Ϣ
			
			return "user/edit";
		}
		
		
		@ResponseBody
		@RequestMapping("/insert")
		public Object insert( User user ) {
			AJAXResult result = new AJAXResult();
			
			try {
				//SimpleDateFormatר����������ʽ��ʱ�����ڵ�
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				user.setCreatetime(sdf.format(new Date()));
				user.setUserpswd("123456");
				userService.insertUser(user);
				result.setSuccess(true);
			} catch ( Exception e ) {
				e.printStackTrace();
				result.setSuccess(false);
			}
			
			return result;
		}
		
		@ResponseBody
		@RequestMapping("/doAssign")
		public Object doAssign( Integer userid, Integer[] unassignroleids ) {
			AJAXResult result = new AJAXResult();
			
			try {
				// ���ӹ�ϵ������
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("userid", userid);
				map.put("roleids", unassignroleids);
				userService.insertUserRoles(map);
				result.setSuccess(true);
			} catch ( Exception e ) {
				e.printStackTrace();
				result.setSuccess(false);
			}
			
			return result;
		}
		
		
		@ResponseBody
		@RequestMapping("/dounAssign")
		public Object dounAssign( Integer userid, Integer[] assignroleids ) {
			AJAXResult result = new AJAXResult();
			
			try {
				// ɾ����ϵ������
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("userid", userid);
				map.put("roleids", assignroleids);
				userService.deleteUserRoles(map);
				result.setSuccess(true);
			} catch ( Exception e ) {
				e.printStackTrace();
				result.setSuccess(false);
			}
			
			return result;
		}
		
		
		
		
		
		@RequestMapping("/index")
		//�û�������Ҫ����ڼ�ҳpageno��ÿҳ�м���
		//@RequestParam(required=false,defaultValue="1")��required�Ǳ�ʾ���Ǳ���Ĳ�����Ĭ��Ϊһ
		//pageno��ǰҳ
		public String index(@RequestParam(required=false,defaultValue="1")Integer pageno,@RequestParam(required=false,defaultValue="2")Integer pagesize,Model model) {
			//��ҳ��ѯ
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("start", (pageno-1)*pagesize);//������ʼλ��
			map.put("size",pagesize);
			
			
			List<User> users = userService.pageQueryData(map);//��ȡ��user����
			model.addAttribute("users",users);//
			//���뵱ǰҳ
			model.addAttribute("pageno",pageno);
			//���ҳ�루��ҳ�룩
			int totalsize=userService.pageQueryCount(map);
			
			int totalno = 0;
			if(totalsize%pagesize==0) {
				totalno = totalsize / pagesize;
				
			}else {
				totalno = totalsize / pagesize + 1;
			}
					
					
			return "user/index2";
		}
		
		@RequestMapping("/assign")
		public String assign( Integer id, Model model ) {
			
			User user = userService.queryById(id);
			model.addAttribute("user", user);
			
			List<Role> roles = roleService.queryAll();//��ȡȫ����ɫ��Ϣ
			
			List<Role> assingedRoles = new ArrayList<Role>();
			List<Role> unassignRoles = new ArrayList<Role>();
			
			// ��ȡ��ϵ�������
			List<Integer> roleids = userService.queryRoleidsByUserid(id);//��ȡ�����û�id����ѡ��ɫ��Ϣ
			for ( Role role : roles ) {//�����еĽ�ɫ��������
				if ( roleids.contains(role.getId()) ) {//�жϱ��������Ľ�ɫ��Ϣ���û��Ƿ�ӵ��
					assingedRoles.add(role);//
				} else {
					unassignRoles.add(role);
				}
			}
			
			model.addAttribute("assingedRoles", assingedRoles);
			model.addAttribute("unassignRoles", unassignRoles);
			
			return "user/assign";
		}
		
		
		@ResponseBody
		@RequestMapping("/pageQuery")
		//queryText��ʾ��ѯ�ı�
		public Object pageQuery( String queryText, Integer pageno, Integer pagesize ) {
			
			AJAXResult result = new AJAXResult();
			
			try {
				
				// ��ҳ��ѯ
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("start", (pageno-1)*pagesize);
				map.put("size", pagesize);
				map.put("queryText", queryText);
				
				List<User> users = userService.pageQueryData( map );
				// ��ǰҳ��			
				// �ܵ���������
				int totalsize = userService.pageQueryCount( map );
				// ���ҳ�루��ҳ�룩
				int totalno = 0;
				if ( totalsize % pagesize == 0 ) {
					totalno = totalsize / pagesize;
				} else {
					totalno = totalsize / pagesize + 1;
				}
				
				// ��ҳ����������
				Page<User> userPage = new Page<User>();
				userPage.setDatas(users);
				userPage.setTotalno(totalno);
				userPage.setTotalsize(totalsize);//����������
				userPage.setPageno(pageno);
				
				result.setData(userPage);
				result.setSuccess(true);
			} catch ( Exception e ) {
				e.printStackTrace();
				result.setSuccess(false);
			}
			
			return result;
			
		}
		
		//����ɾ��
		@ResponseBody
		@RequestMapping("/deletes")
		public Object deletes( Integer[] userid ) {
			AJAXResult result = new AJAXResult();
			
			try {
				
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("userids", userid);
				userService.deleteUsers(map);
				result.setSuccess(true);
			} catch ( Exception e ) {
				e.printStackTrace();
				result.setSuccess(false);
			}
			
			return result;
		}
		
		
		
		
		
		//ɾ�������û�
		@ResponseBody
		@RequestMapping("/delete")
		public Object delete( Integer id ) {
			AJAXResult result = new AJAXResult();
			
			try {
				
				userService.deleteUserById(id);
				result.setSuccess(true);
			} catch ( Exception e ) {
				e.printStackTrace();
				result.setSuccess(false);
			}
			
			return result;
		}
		
		
		@RequestMapping("/index")
		public String index() {
			return "user/index";
			
		}
}
