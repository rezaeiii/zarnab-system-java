package com.zarnab.panel.profile.service;

import com.zarnab.panel.auth.model.User;
import com.zarnab.panel.profile.dto.ProfileDtos;

public interface ProfileService {

    void initiateChangeMobile(ProfileDtos.InitiateChangeMobileRequest request, User user);

    void verifyChangeMobile(ProfileDtos.VerifyChangeMobileRequest request, User user);
}
