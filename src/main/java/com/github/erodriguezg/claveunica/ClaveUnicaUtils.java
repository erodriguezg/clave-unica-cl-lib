package com.github.erodriguezg.claveunica;

import com.github.erodriguezg.claveunica.dto.ConfiguracionClienteDto;
import com.github.erodriguezg.claveunica.dto.UrlClaveUnicaDto;
import com.github.erodriguezg.claveunica.dto.InfoCiudadanoDto;
import com.github.erodriguezg.claveunica.dto.TokenAccesoDto;
import com.github.erodriguezg.claveunica.exceptions.ClaveUnicaException;
import com.github.erodriguezg.claveunica.exceptions.UserInfoClaveUnicaException;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public interface ClaveUnicaUtils {

    /**
     * Método para obtener una url a clave unica.
     * @return La Url de inicio de sesión en clave única, con el state generado para la petición
     */
    UrlClaveUnicaDto generarUrlClaveUnica();

    /**
     * Método que toma los datos de respuesta de clave  única, los valida y entrega un token de acceso
     * @param code código entregado por respuesta clave única
     * @param state state entregado por respuesta clave única
     * @return Un token de acceso para el api de clave única
     * @throws ClaveUnicaException
     */
    TokenAccesoDto solicitarTokenAcceso(String code, String state) throws ClaveUnicaException;

    /**
     * Método que toma los datos de respuesta de clave única, los valida y entrega la datos del ciudadano autentificado.
     * @param code código entregado por respuesta clave única
     * @param state state entregado por respuesta clave única
     * @return Datos del ciudadano autentificado
     * @throws ClaveUnicaException
     */
    InfoCiudadanoDto solicitarInfoCiudadano(String code, String state) throws ClaveUnicaException;

    /**
     * Método que recibe un dto con un token de acceso válido de clave única y entrega la info del ciudadano autentificado.
     * @param tokenAccesoDto token de acceso clave única válido
     * @return Datos del ciudadano autentificado
     * @throws UserInfoClaveUnicaException
     */
    InfoCiudadanoDto solicitarInfoCiudadano(TokenAccesoDto tokenAccesoDto) throws UserInfoClaveUnicaException;



    /**
     * Permite especificar un generador de State
     * @param generator Supplier que retorna el state generado
     */
    void setStateGenerator(Supplier<String> generator);

    /**
     *  Permite especificar un generador de Url hacia clave única custom
     *
     * @param urlGenerator BiFunction de entrada recibe configClienteDto y un String que corresponde al State Generado. Retorna la Url
     */
    void setUrlGenerator(BiFunction<ConfiguracionClienteDto, String, String> urlGenerator);
}
