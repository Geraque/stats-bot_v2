package controller;

import com.example.SpringDemoBot.exception.ResourceNotFoundException;
import com.example.SpringDemoBot.model.Top;
import com.example.SpringDemoBot.repository.TopRepository;
import java.util.ArrayList;
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

//Запросы к таблице с итогами года
@RestController
@RequestMapping("/top")
public class TopController {
	@Autowired
	private TopRepository topRepository;

	//Получение всей информации
	@GetMapping("/all")
	public List<Top> getAllTop() {
		return topRepository.findAll();
	}

	//Получение информации по году
	@GetMapping("/getbyyear/{year}")
	public ResponseEntity<List<String>> getYearTop(@PathVariable(value = "year") int year) {
		List<Top> doceho = topRepository.findAll();
		List<String> list = new ArrayList<>();
		list.add(String.valueOf(year));
		for(Top des: doceho){
			if(des.getYear()==year){
				list.add(des.getName());
				list.add(des.getPlace());
				list.add(String.valueOf(des.getRating()));
			}
		}
		return ResponseEntity.ok().body(list);
	}

	//Получение информации по id
	@GetMapping("/{id}")
	public ResponseEntity<Top> getTopById(@PathVariable(value = "id") Long topId)
			throws ResourceNotFoundException {
		Top top = topRepository.findById(topId)
				.orElseThrow(() -> new ResourceNotFoundException("Top not found for this id : " + topId));
		return ResponseEntity.ok().body(top);
	}

	//Получение информации по имени
	@GetMapping("/getbyname/{name}")
	public ResponseEntity<List<Top>> getTopByName(@PathVariable(value = "name") String topName)
			throws ResourceNotFoundException {
		List<Top> top = topRepository.findAll();
		List<Top> result = new ArrayList<Top>();
		for(Top des: top){
			if(des.getName().equals(topName)){
				result.add(des);
			}
		}
		return ResponseEntity.ok().body(result);
	}

	//Создание итогов
	@PostMapping("/create")
	public Top createTop(@Valid @RequestBody Top top) {
		return topRepository.save(top);
	}

	//Изменение итогов по id
	@PutMapping("/{id}")
	public ResponseEntity<Top> updateTop(@PathVariable(value = "id") Long topId,
			@Valid @RequestBody Top topDetails) throws ResourceNotFoundException {
		Top top = topRepository.findById(topId)
				.orElseThrow(() -> new ResourceNotFoundException("Top not found for this id : " + topId));

		top.setName(topDetails.getName());
		top.setRating(topDetails.getRating());
		top.setYear(topDetails.getYear());
		top.setPlace(topDetails.getPlace());


		final Top updatedTop = topRepository.save(top);
		return ResponseEntity.ok(updatedTop);
	}

	//Удаление информации по id
	@DeleteMapping("/{id}")
	public Map<String, Boolean> deleteTop(@PathVariable(value = "id") Long topId)
			throws ResourceNotFoundException {
		Top top = topRepository.findById(topId)
				.orElseThrow(() -> new ResourceNotFoundException("Top not found for this id : " + topId));

		topRepository.delete(top);
		Map<String, Boolean> response = new HashMap<>();
		response.put("deleted", Boolean.TRUE);
		return response;
	}
}
