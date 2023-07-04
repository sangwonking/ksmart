package ksmart.mybatis.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ksmart.mybatis.dto.Member;
import ksmart.mybatis.dto.MemberLevel;
import ksmart.mybatis.mapper.MemberMapper;
import ksmart.mybatis.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping("/member")
@AllArgsConstructor
public class MemberController {

	private final MemberService memberService;
	private final MemberMapper memberMapper;
		
	@PostConstruct
	public void memberControllerInit() {
		System.out.println("memberController 생성");
	}
	
	@GetMapping("/logout")
	public String logout(HttpSession session) {
		
		// 세션에 담겨져있는 data(정보) 초기화
		session.invalidate();
		
		return "redirect:/member/login";
	}
	
	@PostMapping("/login")
	public String login(@RequestParam(value="memberId") String memberId,
						@RequestParam(value="memberPw") String memberPw,
						HttpServletRequest request,
						HttpServletResponse response,
						HttpSession session,
						RedirectAttributes reAttr) {
		Map<String, Object> validMap = memberService.isValidMember(memberId, memberPw);
		boolean isValid = (boolean) validMap.get("isValid");
		
		if(isValid) {
			Member loginInfo = (Member) validMap.get("memberInfo");
			int memberLevel = loginInfo.getMemberLevel();
			String memberName = loginInfo.getMemberName();

			session.setAttribute("SID", memberId);
			session.setAttribute("SLEVEL", memberLevel);
			session.setAttribute("SNAME", memberName);			
			
			return "redirect:/";
		}
		
		reAttr.addAttribute("msg", "일치하는 회원의 정보가 없습니다.");
		
		return "redirect:/member/login";
	}
	
	@GetMapping("/login")
	public String login(Model model, @RequestParam(value = "msg", required = false) String msg) {
		
		model.addAttribute("title", "로그인");
		if(msg != null) model.addAttribute("msg", msg);
		
		return "login/login";
	}
	
	@GetMapping("/loginHistory")
	@SuppressWarnings("unchecked")
	public String loginHistroy(@RequestParam(value="currentPage", required = false, defaultValue = "1") int currentPage,
							   Model model) {
		Map<String, Object> resultMap = memberService.getLoginHistory(currentPage);
		int lastPage = (int) resultMap.get("lastPage");
		List<Map<String, Object>> loginHistoryList = (List<Map<String, Object>>) resultMap.get("loginHistoryList");
		int startPageNum = (int) resultMap.get("startPageNum");
		int endPageNum = (int) resultMap.get("endPageNum");
		
		model.addAttribute("title", "로그인이력조회");
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("lastPage", lastPage);
		model.addAttribute("loginHistoryList", loginHistoryList);
		model.addAttribute("startPageNum", startPageNum);
		model.addAttribute("endPageNum", endPageNum);
		
		return "login/loginHistory";
	}
	
	@PostMapping("/removeMember")
	public String removeMember(@RequestParam(value="memberId") String memberId,
							   @RequestParam(value="memberPw") String memberPw,
							   RedirectAttributes reAttr) {
		
		// 회원 여부 검증
		Map<String, Object> isValidMap = memberService.isValidMember(memberId, memberPw);
		boolean isValid = (boolean) isValidMap.get("isValid");
		
		// 비밀번호 일치 회원탈퇴
		if(isValid) {
			Member memberInfo = (Member) isValidMap.get("memberInfo");

			// 회원탈퇴 서비스 메소드 호출(숙제: 2023년 06월 26일 확인)
			memberService.removeMember(memberInfo);
			return "redirect:/member/memberList";
		}
		
		reAttr.addAttribute("memberId", memberId);
		reAttr.addAttribute("msg", "회원의 정보가 일치하지 않습니다.");
		
		return "redirect:/member/removeMember";
	}
	
	/**
	 * 회원탈퇴 폼
	 * @param memberId
	 * @return 회원탈퇴 페이지
	 */
	@GetMapping("/removeMember")
	public String removeMember(@RequestParam(value="memberId") String memberId,
							   @RequestParam(value="msg", required = false) String msg,
							   Model model) {
		
		model.addAttribute("title", "회원탈퇴");
		model.addAttribute("memberId", memberId);
		if(msg != null) model.addAttribute("msg", msg);
		
		return "member/removeMember";
	}
	
	/**
	 * 회원 수정 처리
	 * @param Member(커맨드객체) : 사용자 데이터 요청시 (controller layer) 
	 * 		  데이터를 name="값" 과 DTO의 멤버변수명과 동일하면 자동으로 객체생성 후 바인딩 받는 객체
	 * @return 회원리스트화면으로 리디렉션 요청
	 * 
	 */
	@PostMapping("/modifyMember")
	public String modifyMember(Member member) {
		/* 커맨드 객체를 지원을 하지 않았다면 아래와 같은 작업을 반복적으로 작성해야한다.
		String memberId 	= request.getParameter("memberId");
		String memberPw 	= request.getParameter("memberPw");
		int memberLevel 	= Integer.parseInt(request.getParameter("memberLevel"));
		String memberName 	= request.getParameter("memberName");
		String memberAddr 	= request.getParameter("memberAddr");
		String memberEmail 	= request.getParameter("memberEmail");
		String memberRegDate = request.getParameter("memberRegDate");
		
		Member member = new Member();
		member.setMemberId(memberId);
		member.setMemberPw(memberPw);
		member.setMemberLevel(memberLevel);
		member.setMemberName(memberName);
		member.setMemberEmail(memberEmail);
		member.setMemberAddr(memberAddr);
		member.setMemberRegDate(memberRegDate);
		*/
		memberService.modifyMember(member);
		
		return "redirect:/member/memberList";
	}
	
	/**
	 * 사용자 요청 시 쿼리스트링 : ex) memberId=id001
	 * @RequestParam(value="memberId") String memberId
	 * @return
	 */
	@GetMapping("/modifyMember")
	public String modifyMember(@RequestParam(value="memberId") String memberId,
							   Model model) {
		//회원 상세조회
		Member memberInfo = memberService.getMemberInfoById(memberId);
		//회원등급 목록 조회
		List<MemberLevel> memberLevelList = memberService.getMemberLevelList();
		
		model.addAttribute("title", "회원수정");
		model.addAttribute("memberInfo", memberInfo);
		model.addAttribute("memberLevelList", memberLevelList);		
		
		return "member/modifyMember";
	}
	
	@PostMapping("/addMember")
	public String addMember(Member member) {
		
		log.info("회원가입시 입력정보: {}", member);
		
		memberService.addMember(member);
		
		// response.sendRedirect("/member/memberList");
		// spring framework mvc 에서는 controller의 리턴값에 redirect: 키워드로 작성
		// redirect: 키워드를 작성할 경우 그다음의 문자열은 html파일 논리 경로가 아닌 주소를 의미
		return "redirect:/member/memberList";
	}
	
	/**
	 * 아이디 중복 체크
	 * @param memberId
	 * @return
	 */
	@PostMapping("/idCheck")
	@ResponseBody
	public boolean idCheck(@RequestParam(value="memberId") String memberId) {
		log.info("id 중복체크:{}", memberId);
		boolean result = memberMapper.idCheck(memberId);
		log.info("id 중복체크 결과값:{}", result);
		return result;
	}
		
	/**
	 * 회원가입 화면(폼)
	 * @param model
	 * @return
	 */
	@GetMapping("/addMember")
	public String addMember(Model model, HttpSession session) {
		
		String memberId = (String) session.getAttribute("SID");
		
		List<MemberLevel> memberLevelList = memberService.getMemberLevelList();

		if(memberId != null) {
			int memberLevel = (int) session.getAttribute("SLEVEL");
			if(memberLevel > 1) {				
				memberLevelList = memberLevelList.stream()
												 .filter( level -> {
													int levelNum = level.getLevelNum();
													return levelNum == 4;
												 })
												 .toList();
			}
		}else {			
			memberLevelList = memberLevelList.stream()
											 .filter( level -> {
												int levelNum = level.getLevelNum();
												return levelNum == 4;
											 })
											 .toList();
		}
		model.addAttribute("memberLevelList", memberLevelList);
		
		model.addAttribute("title", "회원가입");
						
		return "member/addMember";
	}
	
	/**
	 * 회원목록 조회
	 * @param model
	 * @return
	 */
	@GetMapping("/memberList")
	public String getMemberList(Model model,
								@RequestParam(value="searchKey", required = false, defaultValue = "") String searchKey,
								@RequestParam(value="searchValue", required = false) String searchValue) {
		log.info("searchKey : {}", searchKey);
		log.info("searchValue : {}", searchValue);
		List<Member> memberList = memberService.getMemberList(searchKey, searchValue);
		model.addAttribute("title", "회원목록");
		model.addAttribute("memberList", memberList);
		return "member/memberList";
	}
}






