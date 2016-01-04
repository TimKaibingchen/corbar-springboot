package com.bwts.invoice.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.bwts.common.exception.APIException;
import com.bwts.common.exception.ErrorCode;
import com.bwts.invoice.Dao.JdbcThirdPartyDao;
import com.bwts.invoice.dto.ThirdPartyDTO;
import jersey.repackaged.com.google.common.collect.Maps;

@Service
public class ThirdPartyService {

    private final JdbcThirdPartyDao thirdPartyDao;

    private Map<String, ThirdPartyDTO> thirdPartyMap = Maps.newConcurrentMap();

    private static final int HOUR = 1000 * 60 * 60;

    @Inject
    public ThirdPartyService(JdbcThirdPartyDao thirdPartyDao) {
        this.thirdPartyDao = thirdPartyDao;
    }

    @PostConstruct
    private void init() {
        List<ThirdPartyDTO> thirdParties = thirdPartyDao.getThirdParties();
        for (ThirdPartyDTO thirdParty : thirdParties) {
            thirdPartyMap.put(thirdParty.getPartyCode(), thirdParty);
        }
    }

    public Map<String, ThirdPartyDTO> getThirdPartyMap() {
        return thirdPartyMap;
    }

    public void saveThirdParty(ThirdPartyDTO thirdPartyDTO) {
        thirdPartyMap.put(thirdPartyDTO.getPartyCode(), thirdPartyDTO);
    }

    private ThirdPartyDTO encode(ThirdPartyDTO thirdParty) {
        if (thirdParty.getSecretKey() == null) {
            thirdParty.setSecretKey(SecretKeyHelper.generateKey(thirdParty.getPartyCode()));
        }
        return thirdParty;
    }

    public void createThirdParty(ThirdPartyDTO thirdParty) {
        thirdPartyDao.create(encode(thirdParty));
        saveThirdParty(thirdParty);
    }

    public String generateToken(String code, String key) {
        return TokenHelper.generateToken(code, key);
    }

    public boolean verifyToken(String token) {
        String[] tokenValues = TokenHelper.decodeToken(token);
        return verifyToken(tokenValues);
    }

    public boolean verifyToken(String[] tokenValues) {
        if (tokenValues.length != 3) {
            throw new APIException(HttpStatus.FORBIDDEN, ErrorCode.WRONG_TOKEN_FORMAT, "bwts-token format is wrong");
        }
        String code = tokenValues[0];
        String timeValue = tokenValues[1];
        Long timestamp = Long.valueOf(timeValue);
        if (checkTime(timestamp)) {
            ThirdPartyDTO thirdParty = findThirdParty(code);
            String secretKey = findSecretKey(thirdParty);
            String expectedSignature = TokenHelper.generateSignature(code, timeValue, secretKey);
            if(!expectedSignature.equals(tokenValues[2])) {
                throw new APIException(HttpStatus.FORBIDDEN, ErrorCode.TOKEN_SIGNATURE_NOT_MATCHED, "token signature is not matched");
            }
            return true;
        }
        return false;
    }

    private boolean checkTime(long timestamp) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        if ((now.getTime() - HOUR) > timestamp) {
            throw new APIException(HttpStatus.FORBIDDEN, ErrorCode.EXPIRED_TOKEN, "token validity is out of date");
        }
        return true;
    }

    private ThirdPartyDTO findThirdParty(String code) {
        if (thirdPartyMap.containsKey(code)) {
            return thirdPartyMap.get(code);
        }
        List<ThirdPartyDTO> thirdParties = thirdPartyDao.getThirdPartyByCode(code);
        if (thirdParties != null && thirdParties.size() == 1) {
            return thirdParties.get(0);
        }
        throw new APIException(HttpStatus.FORBIDDEN, ErrorCode.THIRD_PARTY_CODE_NOT_EXIST, "third party code doesn't exist");
    }

    private String findSecretKey(ThirdPartyDTO thirdParty) {
        if (thirdParty != null && thirdParty.getSecretKey() != null) {
            return thirdParty.getSecretKey();
        }
        throw new APIException(HttpStatus.FORBIDDEN, ErrorCode.THIRD_PARTY_SECRET_KEY_NULL, "can't find third party secret key");
    }

}
