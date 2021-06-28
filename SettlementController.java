package jp.co.internous.wasabi.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

import jp.co.internous.wasabi.model.domain.MstDestination;
import jp.co.internous.wasabi.model.mapper.MstDestinationMapper;
import jp.co.internous.wasabi.model.mapper.TblCartMapper;
import jp.co.internous.wasabi.model.mapper.TblPurchaseHistoryMapper;
import jp.co.internous.wasabi.model.session.LoginSession;

@Controller
@RequestMapping("/wasabi/settlement")
public class SettlementController {
	
	private Gson gson = new Gson();
	
	@Autowired
	private LoginSession loginSession;
	
	@Autowired
	private TblCartMapper cartMapper;
	
	@Autowired
	private MstDestinationMapper mstdestinationMapper;
	
	@Autowired
	private TblPurchaseHistoryMapper purchasehistoryMapper;
	
	@RequestMapping("/")
	public String settlement(Model m) {
		
		List<MstDestination> destinations = mstdestinationMapper.findByUserId(loginSession.getUserId());
		m.addAttribute("destinations", destinations);
		m.addAttribute("loginSession", loginSession);
		
		return "settlement";
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping("/complete")
	@ResponseBody
	public boolean complete(@RequestBody String destinationId) {
		// 画面から渡されたdestinationIdを取得
		Map<String, String> map = gson.fromJson(destinationId, Map.class);
		String id = map.get("destinationId");
		
		int userId = loginSession.getUserId();
		Map<String, Object> parameter = new HashMap<>();
		parameter.put("destinationId", id);
		parameter.put("userId", userId);
		int insertCount = purchasehistoryMapper.insert(parameter);
		
		int deleteCount = 0;
		if (insertCount > 0) {
			deleteCount = cartMapper.deleteByUserId(userId);
		}
		return deleteCount == insertCount;
	}

}
