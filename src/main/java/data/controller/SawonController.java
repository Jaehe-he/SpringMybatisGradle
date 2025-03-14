package data.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import data.dto.SawonDto;
import data.service.SawonService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import naver.storage.NcpObjectStorageService;

@Controller
@RequiredArgsConstructor

public class SawonController {
	
	final SawonService sawonService; //final로 해야 Required했을 때 자동 주입됨
	final NcpObjectStorageService storageService; 
	
	private String imagePath="https://kr.object.ncloudstorage.com/bitcamp-bucket-122/sawon/";
	private String bucketName = "bitcamp-bucket-122";
	
	@GetMapping("/")
	public String home() {
		return "sawon/mainpage";
	}
	
	@GetMapping("/list")
	public String sawonList(Model model)
	{
		List<SawonDto> list = sawonService.getSelectAllSawon();
		model.addAttribute("list", list);
		model.addAttribute("totalCount", list.size());
		model.addAttribute("imagePath", imagePath);
		
		return "sawon/sawonlist";		
	}
	
	@GetMapping("/form")
	public String sawonForm()
	{
		return "sawon/sawonform";		
	}
	
	@PostMapping("/insert")
	public String sawonInsert(@ModelAttribute SawonDto dto,
			@RequestParam("upload") MultipartFile upload) {
		if(upload.getOriginalFilename().equals(""))
			dto.setPhoto(null);
		else {
			String photo=storageService.uploadFile(bucketName, "sawon", upload);
			dto.setPhoto(photo);
		}
		sawonService.insertSawon(dto);
		
		return "redirect:./list";
	}
	
	@GetMapping("/delete")
	public String deleteSawon(@RequestParam(value="num") int num, HttpSession session) {
		
		String photo = sawonService.getSawon(num).getPhoto();
		
		if(photo!=null)
			storageService.deleteFile(bucketName, "sawon", photo);
		
		sawonService.deleteSawon(num);
		
		return "redirect:./list";
	}
	
	@GetMapping("/detail")
	public  String detail(@RequestParam(value="num") int num, Model model, HttpSession session) {
		SawonDto dto = sawonService.getSawon(num);
		
		model.addAttribute("dto", dto);
		model.addAttribute("imagePath", imagePath);
		
		return "sawon/sawondetail";
	}
	
	//수정폼으로 이동
	@GetMapping("/updateform")
	public String updateSawon(@RequestParam(value="num") int num, Model model) {
		SawonDto dto = sawonService.getSawon(num);
		
		model.addAttribute("dto", dto);
		model.addAttribute("imagePath", imagePath);
		
		return "sawon/sawonupdate";
	}
	
	//수정할 때 실행되는 컨트롤러
	@PostMapping("/update")
	public String sawonUpdate(@ModelAttribute SawonDto dto,
			@RequestParam("upload") MultipartFile upload) {
		if(upload.getOriginalFilename().equals(""))
			dto.setPhoto(null);
		else {
			String photo=storageService.uploadFile(bucketName, "sawon", upload);
			dto.setPhoto(photo);
		}
		sawonService.updateSawon(dto);
		
		return "redirect:./detail?num="+dto.getNum();
	}
}