package controller;

import com.example.SpringDemoBot.exception.ResourceNotFoundException;
import com.example.SpringDemoBot.model.Match;
import com.example.SpringDemoBot.repository.MatchRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//Запросы к основной таблице со всеми матчами
@RestController
@RequestMapping()
public class MatchController {
	@Autowired
	private MatchRepository matchRepository;

	//Получение всех матчей
	@GetMapping("/matches")
	public List<Match> getAllMatches() {
		return matchRepository.findAll();
	}

	//Получение матчей по id
	@GetMapping("/matches/{id}")
	public ResponseEntity<Match> getMatchById(@PathVariable(value = "id") Long matchId)
			throws ResourceNotFoundException {
		Match match = matchRepository.findById(matchId)
				.orElseThrow(() -> new ResourceNotFoundException("Match not found for this id : " + matchId));
		return ResponseEntity.ok().body(match);
	}

	//Получение матчей по игроку
	@GetMapping("/getbyname/{name}")
	public ResponseEntity<List<Match>> getMatchByName(@PathVariable(value = "name") String matchName)
			throws ResourceNotFoundException {
		//Получаем все матчи и ревёрсим, чтобы первыми шли последние матчи
		List<Match> match = matchRepository.findAll();
		Collections.reverse(match);
		List<Match> result = new ArrayList<Match>();
		int count =0;
		//Выдаём только последние 7 матчей
		for(Match des: match){
			if(des.getName().equals(matchName) && count<7){
				result.add(des);
				count++;
			}
		}
		return ResponseEntity.ok().body(result);
	}

	//Получение всей статистики игрока
	@GetMapping("/getplayerstats/{name}")
	public ResponseEntity<float[]> getPlayerStats(@PathVariable(value = "name") String matchName)
			throws ResourceNotFoundException {
		List<Match> match = matchRepository.findAll();
		float[] arr = new float[15];
		//Распределение информации об игроке по различным категориям
		for(Match des: match){
			if(des.getName().equals(matchName)){
				arr[0]++;
				arr[1]+=des.getRating();
				arr[2]+=des.getSmokeKill();
				arr[3]+=des.getOpenKill();
				arr[4]+=des.getThreeKill();
				arr[5]+=des.getFourKill();
				arr[6]+=des.getAce();
				arr[7]+=des.getFlash();
				arr[8]+=des.getTrade();
				arr[9]+=des.getWallbang();
				arr[10]+=des.getClutchOne();
				arr[11]+=des.getClutchTwo();
				arr[12]+=des.getClutchThree();
				arr[13]+=des.getClutchFour();
				arr[14]+=des.getClutchFive();
			}
		}
		//Вычисление средней статистики за матч, а не общей
		for (int i = 1; i < 15; i++) {
			arr[i]/=arr[0];
		}
		return ResponseEntity.ok().body(arr);
	}


	//Получение всей статистики всех игроков
	@GetMapping("/getallstats")
	public ResponseEntity<float[]> getAllStats()
			throws ResourceNotFoundException {
		List<Match> match = matchRepository.findAll();
		float[] arr = new float[75];
		for(Match des: match){
			switch (des.getName()){
				case "Desmond":
					arr[0]++;
					arr[1]+=des.getRating();
					arr[2]+=des.getSmokeKill();
					arr[3]+=des.getOpenKill();
					arr[4]+=des.getThreeKill();
					arr[5]+=des.getFourKill();
					arr[6]+=des.getAce();
					arr[7]+=des.getFlash();
					arr[8]+=des.getTrade();
					arr[9]+=des.getWallbang();
					arr[10]+=des.getClutchOne();
					arr[11]+=des.getClutchTwo();
					arr[12]+=des.getClutchThree();
					arr[13]+=des.getClutchFour();
					arr[14]+=des.getClutchFive();
					break;
				case "BlackVision":
					arr[15]++;
					arr[16]+=des.getRating();
					arr[17]+=des.getSmokeKill();
					arr[18]+=des.getOpenKill();
					arr[19]+=des.getThreeKill();
					arr[20]+=des.getFourKill();
					arr[21]+=des.getAce();
					arr[22]+=des.getFlash();
					arr[23]+=des.getTrade();
					arr[24]+=des.getWallbang();
					arr[25]+=des.getClutchOne();
					arr[26]+=des.getClutchTwo();
					arr[27]+=des.getClutchThree();
					arr[28]+=des.getClutchFour();
					arr[29]+=des.getClutchFive();
					break;
				case "B4one":
					arr[30]++;
					arr[31]+=des.getRating();
					arr[32]+=des.getSmokeKill();
					arr[33]+=des.getOpenKill();
					arr[34]+=des.getThreeKill();
					arr[35]+=des.getFourKill();
					arr[36]+=des.getAce();
					arr[37]+=des.getFlash();
					arr[38]+=des.getTrade();
					arr[39]+=des.getWallbang();
					arr[40]+=des.getClutchOne();
					arr[41]+=des.getClutchTwo();
					arr[42]+=des.getClutchThree();
					arr[43]+=des.getClutchFour();
					arr[44]+=des.getClutchFive();
					break;
				case "Gloxinia":
					arr[45]++;
					arr[46]+=des.getRating();
					arr[47]+=des.getSmokeKill();
					arr[48]+=des.getOpenKill();
					arr[49]+=des.getThreeKill();
					arr[50]+=des.getFourKill();
					arr[51]+=des.getAce();
					arr[52]+=des.getFlash();
					arr[53]+=des.getTrade();
					arr[54]+=des.getWallbang();
					arr[55]+=des.getClutchOne();
					arr[56]+=des.getClutchTwo();
					arr[57]+=des.getClutchThree();
					arr[58]+=des.getClutchFour();
					arr[59]+=des.getClutchFive();
					break;
				case "221w33":
					arr[60]++;
					arr[61]+=des.getRating();
					arr[62]+=des.getSmokeKill();
					arr[63]+=des.getOpenKill();
					arr[64]+=des.getThreeKill();
					arr[65]+=des.getFourKill();
					arr[66]+=des.getAce();
					arr[67]+=des.getFlash();
					arr[68]+=des.getTrade();
					arr[69]+=des.getWallbang();
					arr[70]+=des.getClutchOne();
					arr[71]+=des.getClutchTwo();
					arr[72]+=des.getClutchThree();
					arr[73]+=des.getClutchFour();
					arr[74]+=des.getClutchFive();
					break;
			}
		}
		for (int i = 1; i < 15; i++) {
			arr[i]/=arr[0];
		}
		for (int i = 16; i < 30; i++) {
			arr[i]/=arr[15];
		}
		for (int i = 31; i < 45; i++) {
			arr[i]/=arr[30];
		}
		for (int i = 46; i < 60; i++) {
			arr[i]/=arr[45];
		}
		for (int i = 61; i < 75; i++) {
			arr[i]/=arr[60];
		}
		return ResponseEntity.ok().body(arr);
	}

	//Получение топ 1 по рейтингу
	@GetMapping("/getrating")
	public ResponseEntity<String[]> getRating()
			throws ResourceNotFoundException {
		List<Match> doceho = matchRepository.findAll();

		float[] desmond = new float[2];
		float[] blackVision = new float[2];
		float[] gloxinia = new float[2];
		float[] tilt = new float[2];
		float[] nekit = new float[2];

		for(Match des: doceho){
			if(des.getName().equals("Desmond")){
				desmond[0]+=1;
				desmond[1]+=des.getRating();
			}
			else if(des.getName().equals("BlackVision")){
				blackVision[0]+=1;
				blackVision[1]+=des.getRating();
			}
			else if(des.getName().equals("B4one")){
				tilt[0]+=1;
				tilt[1]+=des.getRating();
			}
			else if(des.getName().equals("Gloxinia")){
				gloxinia[0]+=1;
				gloxinia[1]+=des.getRating();
			}
			else if (des.getName().equals("221w33")){
				nekit[0]+=1;
				nekit[1]+=des.getRating();
			}
		}
		int num=0;
		float max=desmond[1]/desmond[0];
		float[] arr = {desmond[1]/desmond[0],blackVision[1]/blackVision[0],
				tilt[1]/tilt[0],gloxinia[1]/gloxinia[0],nekit[1]/nekit[0]};
		for(int i =1; i<5;i++){
			if(arr[i] > max){
				num = i;
				max = arr[i];
			}
		}
		//Выяснение топ 1
		String[] res = new String[3];
		if(num==0){
			res = new String[]{"Desmond",String.valueOf(desmond[0]), String.valueOf(desmond[1]/desmond[0])};
		}
		else if(num==1){
			res = new String[]{"BlackVision",String.valueOf(blackVision[0]), String.valueOf(blackVision[1]/blackVision[0])};
		}
		else if(num==2){
			res = new String[]{"B4one",String.valueOf(tilt[0]), String.valueOf(tilt[1]/tilt[0])};
		}
		else if(num==3){
			res = new String[]{"Gloxinia",String.valueOf(gloxinia[0]), String.valueOf(gloxinia[1]/gloxinia[0])};
		}
		else {
			res = new String[]{"221w33",String.valueOf(nekit[0]), String.valueOf(nekit[1]/nekit[0])};
		}
		return ResponseEntity.ok().body(res);
	}

	//Получение топ 1 по опен киллам
	@GetMapping("/getopenkill")
	public ResponseEntity<String[]> getOpenKill()
			throws ResourceNotFoundException {
		List<Match> doceho = matchRepository.findAll();

		float[] desmond = new float[2];
		float[] blackVision = new float[2];
		float[] gloxinia = new float[2];
		float[] tilt = new float[2];
		float[] nekit = new float[2];

		for(Match des: doceho){
			if(des.getName().equals("Desmond")){
				desmond[0]+=1;
				desmond[1]+=des.getOpenKill();
			}
			else if(des.getName().equals("BlackVision")){
				blackVision[0]+=1;
				blackVision[1]+=des.getOpenKill();
			}
			else if(des.getName().equals("B4one")){
				tilt[0]+=1;
				tilt[1]+=des.getOpenKill();
			}
			else if(des.getName().equals("Gloxinia")){
				gloxinia[0]+=1;
				gloxinia[1]+=des.getOpenKill();
			}
			else if (des.getName().equals("221w33")){
				nekit[0]+=1;
				nekit[1]+=des.getOpenKill();
			}
		}
		int num=0;
		float max=desmond[1]/desmond[0];
		float[] arr = {desmond[1]/desmond[0],blackVision[1]/blackVision[0],
				tilt[1]/tilt[0],gloxinia[1]/gloxinia[0],nekit[1]/nekit[0]};
		for(int i =1; i<5;i++){
			if(arr[i] > max){
				num = i;
				max = arr[i];
			}
		}
		String[] res = new String[3];
		if(num==0){
			res = new String[]{"Desmond",String.valueOf(desmond[0]), String.valueOf(desmond[1]/desmond[0])};
		}
		else if(num==1){
			res = new String[]{"BlackVision",String.valueOf(blackVision[0]), String.valueOf(blackVision[1]/blackVision[0])};
		}
		else if(num==2){
			res = new String[]{"B4one",String.valueOf(tilt[0]), String.valueOf(tilt[1]/tilt[0])};
		}
		else if(num==3){
			res = new String[]{"Gloxinia",String.valueOf(gloxinia[0]), String.valueOf(gloxinia[1]/gloxinia[0])};
		}
		else {
			res = new String[]{"221w33",String.valueOf(nekit[0]), String.valueOf(nekit[1]/nekit[0])};
		}
		return ResponseEntity.ok().body(res);
	}

	//Получение топ 1 по флешкам
	@GetMapping("/getflash")
	public ResponseEntity<String[]> getFlash()
			throws ResourceNotFoundException {
		List<Match> doceho = matchRepository.findAll();

		float[] desmond = new float[2];
		float[] blackVision = new float[2];
		float[] gloxinia = new float[2];
		float[] tilt = new float[2];
		float[] nekit = new float[2];

		for(Match des: doceho){
			if(des.getName().equals("Desmond")){
				desmond[0]+=1;
				desmond[1]+=des.getFlash();
			}
			else if(des.getName().equals("BlackVision")){
				blackVision[0]+=1;
				blackVision[1]+=des.getFlash();
			}
			else if(des.getName().equals("B4one")){
				tilt[0]+=1;
				tilt[1]+=des.getFlash();
			}
			else if(des.getName().equals("Gloxinia")){
				gloxinia[0]+=1;
				gloxinia[1]+=des.getFlash();
			}
			else if (des.getName().equals("221w33")){
				nekit[0]+=1;
				nekit[1]+=des.getFlash();
			}
		}
		int num=0;
		float max=desmond[1]/desmond[0];
		float[] arr = {desmond[1]/desmond[0],blackVision[1]/blackVision[0],
				tilt[1]/tilt[0],gloxinia[1]/gloxinia[0],nekit[1]/nekit[0]};
		for(int i =1; i<5;i++){
			if(arr[i] > max){
				num = i;
				max = arr[i];
			}
		}
		String[] res = new String[3];
		if(num==0){
			res = new String[]{"Desmond",String.valueOf(desmond[0]), String.valueOf(desmond[1]/desmond[0])};
		}
		else if(num==1){
			res = new String[]{"BlackVision",String.valueOf(blackVision[0]), String.valueOf(blackVision[1]/blackVision[0])};
		}
		else if(num==2){
			res = new String[]{"B4one",String.valueOf(tilt[0]), String.valueOf(tilt[1]/tilt[0])};
		}
		else if(num==3){
			res = new String[]{"Gloxinia",String.valueOf(gloxinia[0]), String.valueOf(gloxinia[1]/gloxinia[0])};
		}
		else {
			res = new String[]{"221w33",String.valueOf(nekit[0]), String.valueOf(nekit[1]/nekit[0])};
		}
		return ResponseEntity.ok().body(res);
	}

	//Получение топ 1 по размену
	@GetMapping("/gettrade")
	public ResponseEntity<String[]> getTrade()
			throws ResourceNotFoundException {
		List<Match> doceho = matchRepository.findAll();

		float[] desmond = new float[2];
		float[] blackVision = new float[2];
		float[] gloxinia = new float[2];
		float[] tilt = new float[2];
		float[] nekit = new float[2];

		for(Match des: doceho){
			if(des.getName().equals("Desmond")){
				desmond[0]+=1;
				desmond[1]+=des.getTrade();
			}
			else if(des.getName().equals("BlackVision")){
				blackVision[0]+=1;
				blackVision[1]+=des.getTrade();
			}
			else if(des.getName().equals("B4one")){
				tilt[0]+=1;
				tilt[1]+=des.getTrade();
			}
			else if(des.getName().equals("Gloxinia")){
				gloxinia[0]+=1;
				gloxinia[1]+=des.getTrade();
			}
			else if (des.getName().equals("221w33")){
				nekit[0]+=1;
				nekit[1]+=des.getTrade();
			}
		}
		int num=0;
		float max=desmond[1]/desmond[0];
		float[] arr = {desmond[1]/desmond[0],blackVision[1]/blackVision[0],
				tilt[1]/tilt[0],gloxinia[1]/gloxinia[0],nekit[1]/nekit[0]};
		for(int i =1; i<5;i++){
			if(arr[i] > max){
				num = i;
				max = arr[i];
			}
		}
		String[] res = new String[3];
		if(num==0){
			res = new String[]{"Desmond",String.valueOf(desmond[0]), String.valueOf(desmond[1]/desmond[0])};
		}
		else if(num==1){
			res = new String[]{"BlackVision",String.valueOf(blackVision[0]), String.valueOf(blackVision[1]/blackVision[0])};
		}
		else if(num==2){
			res = new String[]{"B4one",String.valueOf(tilt[0]), String.valueOf(tilt[1]/tilt[0])};
		}
		else if(num==3){
			res = new String[]{"Gloxinia",String.valueOf(gloxinia[0]), String.valueOf(gloxinia[1]/gloxinia[0])};
		}
		else {
			res = new String[]{"221w33",String.valueOf(nekit[0]), String.valueOf(nekit[1]/nekit[0])};
		}
		return ResponseEntity.ok().body(res);
	}
	//Получение топ 1 по прострелам
	@GetMapping("/getwallbang")
	public ResponseEntity<String[]> getWallbang()
			throws ResourceNotFoundException {
		List<Match> doceho = matchRepository.findAll();

		float[] desmond = new float[2];
		float[] blackVision = new float[2];
		float[] gloxinia = new float[2];
		float[] tilt = new float[2];
		float[] nekit = new float[2];

		for(Match des: doceho){
			if(des.getName().equals("Desmond")){
				desmond[0]+=1;
				desmond[1]+=des.getWallbang();
			}
			else if(des.getName().equals("BlackVision")){
				blackVision[0]+=1;
				blackVision[1]+=des.getWallbang();
			}
			else if(des.getName().equals("B4one")){
				tilt[0]+=1;
				tilt[1]+=des.getWallbang();
			}
			else if(des.getName().equals("Gloxinia")){
				gloxinia[0]+=1;
				gloxinia[1]+=des.getWallbang();
			}
			else if (des.getName().equals("221w33")){
				nekit[0]+=1;
				nekit[1]+=des.getWallbang();
			}
		}
		int num=0;
		float max=desmond[1]/desmond[0];
		float[] arr = {desmond[1]/desmond[0],blackVision[1]/blackVision[0],
				tilt[1]/tilt[0],gloxinia[1]/gloxinia[0],nekit[1]/nekit[0]};
		for(int i =1; i<5;i++){
			if(arr[i] > max){
				num = i;
				max = arr[i];
			}
		}
		String[] res = new String[3];
		if(num==0){
			res = new String[]{"Desmond",String.valueOf(desmond[0]), String.valueOf(desmond[1]/desmond[0])};
		}
		else if(num==1){
			res = new String[]{"BlackVision",String.valueOf(blackVision[0]), String.valueOf(blackVision[1]/blackVision[0])};
		}
		else if(num==2){
			res = new String[]{"B4one",String.valueOf(tilt[0]), String.valueOf(tilt[1]/tilt[0])};
		}
		else if(num==3){
			res = new String[]{"Gloxinia",String.valueOf(gloxinia[0]), String.valueOf(gloxinia[1]/gloxinia[0])};
		}
		else {
			res = new String[]{"221w33",String.valueOf(nekit[0]), String.valueOf(nekit[1]/nekit[0])};
		}
		return ResponseEntity.ok().body(res);
	}
	//Получение топ 1 по трипл киллам
	@GetMapping("/getthreekill")
	public ResponseEntity<String[]> getThreeKill()
			throws ResourceNotFoundException {
		List<Match> doceho = matchRepository.findAll();

		float[] desmond = new float[2];
		float[] blackVision = new float[2];
		float[] gloxinia = new float[2];
		float[] tilt = new float[2];
		float[] nekit = new float[2];

		for(Match des: doceho){
			if(des.getName().equals("Desmond")){
				desmond[0]+=1;
				desmond[1]+=des.getThreeKill();
			}
			else if(des.getName().equals("BlackVision")){
				blackVision[0]+=1;
				blackVision[1]+=des.getThreeKill();
			}
			else if(des.getName().equals("B4one")){
				tilt[0]+=1;
				tilt[1]+=des.getThreeKill();
			}
			else if(des.getName().equals("Gloxinia")){
				gloxinia[0]+=1;
				gloxinia[1]+=des.getThreeKill();
			}
			else if (des.getName().equals("221w33")){
				nekit[0]+=1;
				nekit[1]+=des.getThreeKill();
			}
		}
		int num=0;
		float max=desmond[1]/desmond[0];
		float[] arr = {desmond[1]/desmond[0],blackVision[1]/blackVision[0],
				tilt[1]/tilt[0],gloxinia[1]/gloxinia[0],nekit[1]/nekit[0]};
		for(int i =1; i<5;i++){
			if(arr[i] > max){
				num = i;
				max = arr[i];
			}
		}
		String[] res = new String[3];
		if(num==0){
			res = new String[]{"Desmond",String.valueOf(desmond[0]), String.valueOf(desmond[1]/desmond[0])};
		}
		else if(num==1){
			res = new String[]{"BlackVision",String.valueOf(blackVision[0]), String.valueOf(blackVision[1]/blackVision[0])};
		}
		else if(num==2){
			res = new String[]{"B4one",String.valueOf(tilt[0]), String.valueOf(tilt[1]/tilt[0])};
		}
		else if(num==3){
			res = new String[]{"Gloxinia",String.valueOf(gloxinia[0]), String.valueOf(gloxinia[1]/gloxinia[0])};
		}
		else {
			res = new String[]{"221w33",String.valueOf(nekit[0]), String.valueOf(nekit[1]/nekit[0])};
		}
		return ResponseEntity.ok().body(res);
	}
	//Получение топ 1 по квадро киллам
	@GetMapping("/getfourkill")
	public ResponseEntity<String[]> getFourKill()
			throws ResourceNotFoundException {
		List<Match> doceho = matchRepository.findAll();

		float[] desmond = new float[2];
		float[] blackVision = new float[2];
		float[] gloxinia = new float[2];
		float[] tilt = new float[2];
		float[] nekit = new float[2];

		for(Match des: doceho){
			if(des.getName().equals("Desmond")){
				desmond[0]+=1;
				desmond[1]+=des.getFourKill();
			}
			else if(des.getName().equals("BlackVision")){
				blackVision[0]+=1;
				blackVision[1]+=des.getFourKill();
			}
			else if(des.getName().equals("B4one")){
				tilt[0]+=1;
				tilt[1]+=des.getFourKill();
			}
			else if(des.getName().equals("Gloxinia")){
				gloxinia[0]+=1;
				gloxinia[1]+=des.getFourKill();
			}
			else if (des.getName().equals("221w33")){
				nekit[0]+=1;
				nekit[1]+=des.getFourKill();
			}
		}
		int num=0;
		float max=desmond[1]/desmond[0];
		float[] arr = {desmond[1]/desmond[0],blackVision[1]/blackVision[0],
				tilt[1]/tilt[0],gloxinia[1]/gloxinia[0],nekit[1]/nekit[0]};
		for(int i =1; i<5;i++){
			if(arr[i] > max){
				num = i;
				max = arr[i];
			}
		}
		String[] res = new String[3];
		if(num==0){
			res = new String[]{"Desmond",String.valueOf(desmond[0]), String.valueOf(desmond[1]/desmond[0])};
		}
		else if(num==1){
			res = new String[]{"BlackVision",String.valueOf(blackVision[0]), String.valueOf(blackVision[1]/blackVision[0])};
		}
		else if(num==2){
			res = new String[]{"B4one",String.valueOf(tilt[0]), String.valueOf(tilt[1]/tilt[0])};
		}
		else if(num==3){
			res = new String[]{"Gloxinia",String.valueOf(gloxinia[0]), String.valueOf(gloxinia[1]/gloxinia[0])};
		}
		else {
			res = new String[]{"221w33",String.valueOf(nekit[0]), String.valueOf(nekit[1]/nekit[0])};
		}
		return ResponseEntity.ok().body(res);
	}
	//Получение топ 1 по эйсам
	@GetMapping("/getace")
	public ResponseEntity<String[]> getAce()
			throws ResourceNotFoundException {
		List<Match> doceho = matchRepository.findAll();

		float[] desmond = new float[2];
		float[] blackVision = new float[2];
		float[] gloxinia = new float[2];
		float[] tilt = new float[2];
		float[] nekit = new float[2];

		for(Match des: doceho){
			if(des.getName().equals("Desmond")){
				desmond[0]+=1;
				desmond[1]+=des.getAce();
			}
			else if(des.getName().equals("BlackVision")){
				blackVision[0]+=1;
				blackVision[1]+=des.getAce();
			}
			else if(des.getName().equals("B4one")){
				tilt[0]+=1;
				tilt[1]+=des.getAce();
			}
			else if(des.getName().equals("Gloxinia")){
				gloxinia[0]+=1;
				gloxinia[1]+=des.getAce();
			}
			else if (des.getName().equals("221w33")){
				nekit[0]+=1;
				nekit[1]+=des.getAce();
			}
		}
		int num=0;
		float max=desmond[1]/desmond[0];
		float[] arr = {desmond[1]/desmond[0],blackVision[1]/blackVision[0],
				tilt[1]/tilt[0],gloxinia[1]/gloxinia[0],nekit[1]/nekit[0]};
		for(int i =1; i<5;i++){
			if(arr[i] > max){
				num = i;
				max = arr[i];
			}
		}
		String[] res = new String[3];
		if(num==0){
			res = new String[]{"Desmond",String.valueOf(desmond[0]), String.valueOf(desmond[1]/desmond[0])};
		}
		else if(num==1){
			res = new String[]{"BlackVision",String.valueOf(blackVision[0]), String.valueOf(blackVision[1]/blackVision[0])};
		}
		else if(num==2){
			res = new String[]{"B4one",String.valueOf(tilt[0]), String.valueOf(tilt[1]/tilt[0])};
		}
		else if(num==3){
			res = new String[]{"Gloxinia",String.valueOf(gloxinia[0]), String.valueOf(gloxinia[1]/gloxinia[0])};
		}
		else {
			res = new String[]{"221w33",String.valueOf(nekit[0]), String.valueOf(nekit[1]/nekit[0])};
		}
		return ResponseEntity.ok().body(res);
	}
	//Получение топ 1 по клатчам
	@GetMapping("/getclutches")
	public ResponseEntity<String[]> getClutches()
			throws ResourceNotFoundException {
		List<Match> doceho = matchRepository.findAll();

		int[] desmond = new int[7];
		int[] blackVision = new int[7];
		int[] gloxinia = new int[7];
		int[] tilt = new int[7];
		int[] nekit = new int[7];

		for(Match des: doceho){
			if(des.getName().equals("Desmond")){
				desmond[0]+=des.getClutchOne()+des.getClutchTwo()+ des.getClutchThree()+ des.getClutchFour()+des.getClutchFive();
				desmond[1]+=des.getClutchOne();
				desmond[2]+=des.getClutchTwo();
				desmond[3]+=des.getClutchThree();
				desmond[4]+=des.getClutchFour();
				desmond[5]+=des.getClutchFive();
				desmond[6]+=1;
			}
			else if(des.getName().equals("BlackVision")){
				blackVision[0]+=des.getClutchOne()+des.getClutchTwo()+ des.getClutchThree()+ des.getClutchFour()+des.getClutchFive();
				blackVision[1]+=des.getClutchOne();
				blackVision[2]+=des.getClutchTwo();
				blackVision[3]+=des.getClutchThree();
				blackVision[4]+=des.getClutchFour();
				blackVision[5]+=des.getClutchFive();
				blackVision[6]+=1;
			}
			else if(des.getName().equals("B4one")){
				tilt[0]+=des.getClutchOne()+des.getClutchTwo()+ des.getClutchThree()+ des.getClutchFour()+des.getClutchFive();
				tilt[1]+=des.getClutchOne();
				tilt[2]+=des.getClutchTwo();
				tilt[3]+=des.getClutchThree();
				tilt[4]+=des.getClutchFour();
				tilt[5]+=des.getClutchFive();
				tilt[6]+=1;
			}
			else if(des.getName().equals("Gloxinia")){
				gloxinia[0]+=des.getClutchOne()+des.getClutchTwo()+ des.getClutchThree()+ des.getClutchFour()+des.getClutchFive();
				gloxinia[1]+=des.getClutchOne();
				gloxinia[2]+=des.getClutchTwo();
				gloxinia[3]+=des.getClutchThree();
				gloxinia[4]+=des.getClutchFour();
				gloxinia[5]+=des.getClutchFive();
				gloxinia[6]+=1;
			}
			else if (des.getName().equals("221w33")){
				nekit[0]+=des.getClutchOne()+des.getClutchTwo()+ des.getClutchThree()+ des.getClutchFour()+des.getClutchFive();
				nekit[1]+=des.getClutchOne();
				nekit[2]+=des.getClutchTwo();
				nekit[3]+=des.getClutchThree();
				nekit[4]+=des.getClutchFour();
				nekit[5]+=des.getClutchFive();
				nekit[6]+=1;
			}
		}
		int num=0;
		int max=desmond[0];
		int[] arr = {desmond[0],blackVision[0],tilt[0],gloxinia[0],nekit[0]};
		for(int i =1; i<5;i++){
			if(arr[i] > max){
				num = i;
				max = arr[i];
			}
		}
		String[] res = new String[8];
		if(num==0){
			res = new String[]{"Desmond",String.valueOf(desmond[0]), String.valueOf(desmond[1]),
					String.valueOf(desmond[2]), String.valueOf(desmond[3]),
					String.valueOf(desmond[4]), String.valueOf(desmond[5]), String.valueOf(desmond[6])};
		}
		else if(num==1){
			res = new String[]{"BlackVision",String.valueOf(blackVision[0]), String.valueOf(blackVision[1]),
					String.valueOf(blackVision[2]), String.valueOf(blackVision[3]),
					String.valueOf(blackVision[4]), String.valueOf(blackVision[5]), String.valueOf(blackVision[6])};
		}
		else if(num==2){
			res = new String[]{"B4one",String.valueOf(tilt[0]), String.valueOf(tilt[1]),
					String.valueOf(tilt[2]), String.valueOf(tilt[3]),
					String.valueOf(tilt[4]), String.valueOf(tilt[5]), String.valueOf(tilt[6])};
		}
		else if(num==3){
			res = new String[]{"Gloxinia",String.valueOf(gloxinia[0]), String.valueOf(gloxinia[1]),
					String.valueOf(gloxinia[2]), String.valueOf(gloxinia[3]),
					String.valueOf(gloxinia[4]), String.valueOf(gloxinia[5]), String.valueOf(gloxinia[6])};
		}
		else {
			res = new String[]{"221w33",String.valueOf(nekit[0]), String.valueOf(nekit[1]),
					String.valueOf(nekit[2]), String.valueOf(nekit[3]),
					String.valueOf(nekit[4]), String.valueOf(nekit[5]), String.valueOf(nekit[6])};
		}
		return ResponseEntity.ok().body(res);
	}
	//Создание матча
	@PostMapping("/matches")
	public Match createMatch(@Valid @RequestBody Match match) {
		return matchRepository.save(match);
	}

	//Изменение матча по id
	@PutMapping("/matches/{id}")
	public ResponseEntity<Match> updateMatch(@PathVariable(value = "id") Long desmondId,
											   @Valid @RequestBody Match matchDetails) throws ResourceNotFoundException {
		Match match = matchRepository.findById(desmondId)
				.orElseThrow(() -> new ResourceNotFoundException("Desmond not found for this id : " + desmondId));

		match.setName(matchDetails.getName());
		match.setData(matchDetails.getData());
		match.setRating(matchDetails.getRating());
		match.setSmokeKill(matchDetails.getSmokeKill());
		match.setOpenKill(matchDetails.getOpenKill());
		match.setThreeKill(matchDetails.getThreeKill());
		match.setFourKill(matchDetails.getFourKill());
		match.setAce(matchDetails.getAce());
		match.setFlash(matchDetails.getFlash());
		match.setTrade(matchDetails.getThreeKill());
		match.setWallbang(matchDetails.getWallbang());
		match.setClutchOne(matchDetails.getClutchOne());
		match.setClutchTwo(matchDetails.getClutchTwo());
		match.setClutchThree(matchDetails.getClutchThree());
		match.setClutchFour(matchDetails.getClutchFour());
		match.setClutchFive(matchDetails.getClutchFive());
		match.setType(matchDetails.getType());


		final Match updatedMatch = matchRepository.save(match);
		return ResponseEntity.ok(updatedMatch);
	}

	//Удаление матча по id
	@DeleteMapping("/matches/{id}")
	public Map<String, Boolean> deleteMatch(@PathVariable(value = "id") Long desmondId)
			throws ResourceNotFoundException {
		Match match = matchRepository.findById(desmondId)
				.orElseThrow(() -> new ResourceNotFoundException("Desmond not found for this id : " + desmondId));

		matchRepository.delete(match);
		Map<String, Boolean> response = new HashMap<>();
		response.put("deleted", Boolean.TRUE);
		return response;
	}
}
