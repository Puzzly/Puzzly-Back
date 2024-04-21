package com.puzzly.api.enums;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EnumTypeHandler <E extends Enum<E> & CodeEnum> implements TypeHandler<CodeEnum>{

    private Class<E> type;

    public EnumTypeHandler(Class<E> type) {
        this.type = type;
    }

    @Override
    public void setParameter(PreparedStatement ps, int i, CodeEnum parameter, JdbcType jdbcType) throws SQLException {
        if(parameter != null) {
            ps.setString(i, parameter.getText());
        }
    }

    @Override
    public CodeEnum getResult(ResultSet rs, String columnName) throws SQLException {
        String code = rs.getString(columnName);
        if(rs.wasNull()) {
            return null;
        }
        return getCodeEnum(code);
    }

    @Override
    public CodeEnum getResult(ResultSet rs, int columnIndex) throws SQLException {
        String code = rs.getString(columnIndex);
        if (rs.wasNull()) {
            return null;
        }

        return getCodeEnum(code);
    }

    @Override
    public CodeEnum getResult(CallableStatement cs, int columnIndex) throws SQLException {
        String code = cs.getString(columnIndex);
        if (cs.wasNull()) {
            return null;
        }

        return getCodeEnum(code);
    }

    private CodeEnum getCodeEnum(String code) {
        try {
            CodeEnum[] enumConstants = type.getEnumConstants();
            for (CodeEnum codeNum : enumConstants) {
                if (codeNum.getText() == code) {
                    return codeNum;
                }
            }

            return null;
        } catch(Exception e) {
            throw new IllegalArgumentException("invalid code : " + code);
        }
    }

}