package com.ead.course.services.impl;

import com.ead.course.repositories.ModuleRepository;
import org.springframework.stereotype.Service;

@Service
public class ModuleServiceImpl {

    final ModuleRepository moduleRepository;

    public ModuleServiceImpl(ModuleRepository moduleRepository) {
        this.moduleRepository = moduleRepository;
    }
}
