package com.bwts.invoice.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class ThirdPartyDTO {
    @XmlElement(name = "Code")
    private String partyCode;

    @XmlElement(name = "Description")
    private String partyDesc;

    @XmlElement(name = "SecretKey")
    private String secretKey;

    public ThirdPartyDTO(String partyDesc, String partyCode, String secretKey) {
        this.partyDesc = partyDesc;
        this.partyCode = partyCode;
        this.secretKey = secretKey;
    }

    public ThirdPartyDTO() {
    }

    public String getPartyCode() {
        return partyCode;
    }

    public void setPartyCode(String partyCode) {
        this.partyCode = partyCode;
    }

    public String getPartyDesc() {
        return partyDesc;
    }

    public void setPartyDesc(String partyDesc) {
        this.partyDesc = partyDesc;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}
