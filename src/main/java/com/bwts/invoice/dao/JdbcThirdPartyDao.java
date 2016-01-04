package com.bwts.invoice.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.bwts.invoice.dto.ThirdPartyDTO;

@Repository
public class JdbcThirdPartyDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcThirdPartyDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<ThirdPartyDTO> getThirdParties() {
        return jdbcTemplate.query("SELECT * FROM third_party", THIRD_PARTY_ROW_MAPPER);
    }

    public List<ThirdPartyDTO> getThirdPartyByCode(String code) {
        String sql = "SELECT * FROM third_party WHERE third_party_code = ?";
        return jdbcTemplate.query(sql, THIRD_PARTY_ROW_MAPPER, code);

    }

    public void create(ThirdPartyDTO thirdParty) {
        String sql = "INSERT INTO third_party(third_party_code, third_party_desc, secret_key) VALUES (?,?,?)";
        jdbcTemplate.update(sql, thirdParty.getPartyCode(), thirdParty.getPartyDesc(), thirdParty.getSecretKey());

    }

    static final RowMapper<ThirdPartyDTO> THIRD_PARTY_ROW_MAPPER = new RowMapper<ThirdPartyDTO>() {
        @Override
        public ThirdPartyDTO mapRow(ResultSet rs, int num) throws SQLException {
            ThirdPartyDTO thirdParty = new ThirdPartyDTO();
            thirdParty.setPartyCode(rs.getString("third_party_code"));
            thirdParty.setPartyDesc(rs.getString("third_party_desc"));
            thirdParty.setSecretKey(rs.getString("secret_key"));
            return thirdParty;
        }
    };
}
