package com.example.Project_Tracker.service;

import com.example.Project_Tracker.dto.IbuDto;
import com.example.Project_Tracker.entity.Ibu;
import com.example.Project_Tracker.exceptions.ResourceNotFoundException;
import com.example.Project_Tracker.repository.IbuRepository;
import com.example.Project_Tracker.repository.ProjectRepository;
import com.example.Project_Tracker.repository.UserRepository;
import com.example.Project_Tracker.utils.MapperUtils;
import lombok.RequiredArgsConstructor;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IbuService {

    private final IbuRepository ibuRepository;
    private final MapperUtils mapperUtils;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;


    public List<IbuDto> getAllIbus() {
        List<Ibu> ibus = ibuRepository.findAll();
        return mapperUtils.mapList(ibus, IbuDto.class);
    }

    public IbuDto createIbu(IbuDto ibuDto) {
        Ibu ibu = mapperUtils.map(ibuDto, Ibu.class);
        // ibu.setCreatedAt(LocalDateTime.now());
        Ibu savedIbu = ibuRepository.save(ibu);
        return mapperUtils.map(savedIbu, IbuDto.class);
    }

    // ✅ Delete IBU
    public void deleteIbu(String id) {
        if (!ibuRepository.existsById(id)) {
            if (!ibuRepository.existsById(id)) {
                throw new ResourceNotFoundException("IBU not found with ID: " + id);
            }
            if (userRepository.existsByIbu_Id(id) || projectRepository.existsByIbu_Id(id)) {
                throw new DataIntegrityViolationException("IBU linked to users or projects");
            }

        }
        ibuRepository.deleteById(id);
    }
}
