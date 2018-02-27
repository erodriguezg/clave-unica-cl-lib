# clave-unica-cl-lib
Componentes utilitarios para integración con clave única de gobierno chile

## Forma de uso

### 1) Crear instancia de configuración: 

Clase: com.github.erodriguezg.claveunica.dto.ConfiguracionClienteDto

ejemplo:

<code>
    ConfiguracionClienteDto config = new ConfiguracionClienteDto(); <br/>
    config.setClientId(this.clientId); <br/>
    config.setClientSecret(this.clientSecret); <br/>
    config.setEndpoint(this.endPoint); <br/>
    config.setRedirectUrl(redirectUrl);<br/>
    <br/>
</code>

parametros: 
* clientid: identificador entregado por clave única. ejemplo:  'ed5105f72fc843179463f1cfdca25e22'
* clientsecret: identificador entregado por clave única. Ejemplo: 000c1a54b9034d13a4bdf59a5151e950
* endpoint: Dominio base de clave única. Actualmente: https://accounts.claveunica.gob.cl
* redirecturl: URL donde debera responder clave única http://mi-sistema.com/claveunica/callback


### 2) Crear instancia de ClaveUnicaUtils:

Clase: com.github.erodriguezg.claveunica.ClaveUnicaUtils

ejemplo:

<code>
ClaveUnicaUtils(configDto, new HttpClientUtils());
<br/><br/>
</code>

parametros:
* configDto: Objeto de configuración creado en el paso 1
* httpClientUtils: instancia de la clase 'com.github.erodriguezg.http.HttpClientUtils' que permite realizar conexiónes https sin necesidad de certificados.

### 3) Obtener DTO de redirección a Clave Única

Obtener BotonClaveUnicaDto el cual contiene la información de redirección a clave única (url) junto con un UUID unico generado (state)

<code>
BotonClaveUnicaDto dto = claveUnicaUtils.generarUrlClaveUnica()
<br/><br/>
</code>

Este state debe ser almacenado en la sesión http del usuario o alguna otra forma de memoria de sesión, ya que sera utilizado más adelante en el flujo para verificar la respuesta de clave única.

### 4) Procesar Respuesta Clave Única 

Con el utilitario de ClaveUnicaUtils instanciado en el paso 2 se puede obtener la información del usuario identificado en clave única

<code>
InfoCiudadanoDto infoCiudadanoDto = claveUnicaUtils.solicitarInfoCiudadano(codeClaveUnica, stateClaveUnica, stateLocal);
<br/><br/>
</code>

Parametros:
* codeClaveUnica: corresponde al código de respuesta entregado en la redirección realizada por clave única.
* stateClaveUnica: corresponde al código state entregado en la redirección realizada por clave única
* stateLocal: corresponde al state generado en el paso 3 desde el BotonClaveUnica

Los primeros 2 parametros son entregados en la URL de redirección de clave única, como parametros GET. 

Salida:
* InfoCiudadanoDto: Corresponde a la estructura de respuesta entregada por clave única, con los nombres y run del usuario identificado.


## Integración con Spring

* Para el paso 1 y 2 se debe crear instancias singleton de los objetos. Ejemplo:

<code>
import com.github.erodriguezg.claveunica.ClaveUnicaUtils; <br/>
import com.github.erodriguezg.claveunica.dto.ConfiguracionClienteDto; <br/>
import com.github.erodriguezg.http.HttpClientUtils; <br/>
import org.springframework.beans.factory.annotation.Value; <br/>
import org.springframework.context.annotation.Bean; <br/>
import org.springframework.context.annotation.Configuration; <br/>
<br/>
@Configuration <br/>
public class ClaveUnicaConfig { <br/>
<br/>
   @Value("${app.claveunica.clientid}") <br/>
   private String clientId; <br/>
<br/>
   @Value("${app.claveunica.clientsecret}")<br/>
   private String clientSecret;<br/>
<br/>
   @Value("${app.claveunica.endpoint}")<br/>
   private String endPoint;<br/>
<br/>
   @Value("${app.claveunica.redirecturl}")<br/>
   private String redirectUrl;<br/>
<br/>
   @Bean<br/>
   public ConfiguracionClienteDto configuracionClienteDto() {<br/>
        ConfiguracionClienteDto config = new ConfiguracionClienteDto();<br/>
        config.setClientId(this.clientId);<br/>
        config.setClientSecret(this.clientSecret);<br/>
        config.setEndpoint(this.endPoint);<br/>
        config.setRedirectUrl(redirectUrl);<br/>
        return config;<br/>
    }<br/>
<br/>
   @Bean<br/>
   public ClaveUnicaUtils claveUnicaUtils(ConfiguracionClienteDto configDto) {<br/>
       return new ClaveUnicaUtils(configDto, new HttpClientUtils());<br/>
   }<br/>
<br/>
}<br/>
</code>

* Para el paso 4 es necesario construir un controlador REST con la url de redirección de clave única. Ejemplo:

<code>
@GetMapping("/callback")<br/>
public void callBackClaveUnica(<br/>
		@RequestParam(value = "code", required = false) String codeClaveUnica,<br/>
		@RequestParam(value = "state", required = false) String stateClaveUnica,<br/>
		@RequestParam(value = "error", required = false, defaultValue = "false") boolean error,<br/>
		HttpServletResponse httpResponse,<br/>
		HttpSession httpSession) {<br/><br/>
	if (error) {<br/>
		log.warn("error respuesta clave unica. sin detalles");<br/>
		invalidarSesionYRedireccionarInicio(httpSession, httpResponse);<br/>
		return;<br/>
	}<br/>
<br/>
    String stateLocal = (String) httpSession.getAttribute(ConstantesUtil.CLAVEUNICA_STATE_PARAM_NAME);<br/>
	InfoCiudadanoDto info;
	try {
		info = claveUnicaService.obtenerInfoCiudadano(codeClaveUnica, stateClaveUnica, stateLocal);
	} catch (ClaveUnicaException e) {<br/>
		log.error("error obtener info ciudadano: ", e);<br/>
		invalidarSesionYRedireccionarInicio(httpSession, httpResponse);<br/>
		return;<br/>
	}<br/>
	if (info == null || info.getRolUnico() == null || info.getRolUnico().getNumero() == null) {<br/>
		log.error("no viene información minima para identificar ciudadano (run)");<br/>
		invalidarSesionYRedireccionarInicio(httpSession, httpResponse);<br/>
		return;<br/>
	}<br/>

	String nombres = (info.getName() != null && info.getName().getNombresList() != null) ?
			StringUtils.join(info.getName().getNombresList(), ' ') : "";

	String apellidos = (info.getName() != null && info.getName().getApellidosList() != null) ?
			StringUtils.join(info.getName().getApellidosList(), ' ') : "";

	procesarIngresoClaveUnica(info.getRolUnico().getNumero(), nombres, apellidos, httpSession, httpResponse);
}
<code/>


## Instalación desde Repositorio Maven


* Agregar repositorio:
```xml 
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

* Dependencia:

```xml
    <dependency>
        <groupId>com.github.erodriguezg</groupId>
        <artifactId>clave-unica-cl-lib</artifactId>
        <version>${version.erodriguezg-claveunica}</version>
    </dependency>
```

donde ```${version.erodriguez-claveunica}``` corresponde a un release (tag)

* Otras dependencias necesarias:

```xml
<dependencies>
    <dependency>
        <groupId>org.apache.httpcomponents</groupId>
        <artifactId>httpclient</artifactId>
        <version>${version.httpclient}</version>
        <scope>provided</scope>
    </dependency>
    
    <dependency>
        <groupId>commons-io</groupId>
        <artifactId>commons-io</artifactId>
        <version>${version.commonsio}</version>
        <scope>provided</scope>
    </dependency>
    
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
        <version>${version.jackson}</version>
        <scope>provided</scope>
    </dependency>
    
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-simple</artifactId>
        <version>${version.slf4j}</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

versiones
```xml
<properties>
    <version.httpclient>4.4.1</version.httpclient>
    <version.commonsio>2.4</version.commonsio>
    <version.jackson>2.8.11</version.jackson>
    <version.slf4j>1.7.25</version.slf4j>
    <version.junit>4.12</version.junit>
</properties>
```



