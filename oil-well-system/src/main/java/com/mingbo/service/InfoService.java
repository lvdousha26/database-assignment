package com.mingbo.service;

import com.auth0.jwt.interfaces.Claim;
import com.mingbo.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class InfoService {

    @Autowired
    private HttpServletRequest request;


    public long getOperateUser() {
        if (false) {
            return 2;
        }
        String jwt = request.getHeader("token");
        Map<String, Claim> claims = JwtUtils.parseJWT(jwt);
        Claim idClaim = claims.get("id");
        return idClaim.asLong();
    }
}
