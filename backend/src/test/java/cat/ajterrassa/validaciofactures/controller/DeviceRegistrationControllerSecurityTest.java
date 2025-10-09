package cat.ajterrassa.validaciofactures.controller;

import cat.ajterrassa.validaciofactures.model.DeviceRegistration;
import cat.ajterrassa.validaciofactures.model.DeviceRegistrationStatus;
import cat.ajterrassa.validaciofactures.repository.DeviceRegistrationRepository;
import cat.ajterrassa.validaciofactures.repository.UsuariRepository;
import cat.ajterrassa.validaciofactures.security.JwtFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DeviceRegistrationControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeviceRegistrationRepository deviceRegistrationRepository;

    @MockBean
    private UsuariRepository usuariRepository;

    @MockBean
    private JwtFilter jwtFilter;

    @BeforeEach
    void setUpJwtFilter() throws Exception {
        Mockito.doAnswer(invocation -> {
            HttpServletRequest request = invocation.getArgument(0);
            HttpServletResponse response = invocation.getArgument(1);
            FilterChain chain = invocation.getArgument(2);
            chain.doFilter(request, response);
            return null;
        }).when(jwtFilter).doFilter(any(HttpServletRequest.class), any(HttpServletResponse.class), any(FilterChain.class));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMINISTRADOR"})
    void listDevicesAccessibleForAdministrators() throws Exception {
        // Mock dispositiu aprovat per passar DeviceAuthorizationFilter
        when(deviceRegistrationRepository.findByFid("test-approved-device"))
                .thenReturn(Optional.of(DeviceRegistration.builder()
                        .fid("test-approved-device")
                        .status(DeviceRegistrationStatus.APPROVED)
                        .build()));
        
        when(deviceRegistrationRepository.findAll()).thenReturn(List.of(DeviceRegistration.builder()
                .fid("fid-1")
                .status(DeviceRegistrationStatus.PENDING)
                .build()));

        mockMvc.perform(get("/api/admin/devices")
                .header("X-Firebase-Installation-Id", "test-approved-device"))
                .andExpect(status().isOk());

        verify(deviceRegistrationRepository).findAll();
    }

    @Test
    @WithMockUser(username = "user", roles = {"GESTOR"})
    void listDevicesForbiddenForNonAdministrators() throws Exception {
        mockMvc.perform(get("/api/admin/devices"))
                .andExpect(status().isForbidden());

        verify(deviceRegistrationRepository, never()).findAll();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMINISTRADOR"})
    void approveDeviceAllowedForAdministrators() throws Exception {
        // Mock dispositiu aprovat per passar DeviceAuthorizationFilter
        when(deviceRegistrationRepository.findByFid("test-approved-device"))
                .thenReturn(Optional.of(DeviceRegistration.builder()
                        .fid("test-approved-device")
                        .status(DeviceRegistrationStatus.APPROVED)
                        .build()));
        
        DeviceRegistration registration = DeviceRegistration.builder()
                .fid("fid-approve")
                .status(DeviceRegistrationStatus.PENDING)
                .build();
        when(deviceRegistrationRepository.findByFid("fid-approve")).thenReturn(Optional.of(registration));

        mockMvc.perform(post("/api/admin/devices/{fid}/approve", "fid-approve")
                .header("X-Firebase-Installation-Id", "test-approved-device"))
                .andExpect(status().isOk());

        verify(deviceRegistrationRepository).save(registration);
    }

    @Test
    @WithMockUser(username = "user", roles = {"GESTOR"})
    void approveDeviceForbiddenForNonAdministrators() throws Exception {
        mockMvc.perform(post("/api/admin/devices/{fid}/approve", "fid-approve"))
                .andExpect(status().isForbidden());

        verify(deviceRegistrationRepository, never()).findByFid("fid-approve");
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMINISTRADOR"})
    void revokeDeviceAllowedForAdministrators() throws Exception {
        // Mock dispositiu aprovat per passar DeviceAuthorizationFilter
        when(deviceRegistrationRepository.findByFid("test-approved-device"))
                .thenReturn(Optional.of(DeviceRegistration.builder()
                        .fid("test-approved-device")
                        .status(DeviceRegistrationStatus.APPROVED)
                        .build()));
        
        DeviceRegistration registration = DeviceRegistration.builder()
                .fid("fid-revoke")
                .status(DeviceRegistrationStatus.PENDING)
                .build();
        when(deviceRegistrationRepository.findByFid("fid-revoke")).thenReturn(Optional.of(registration));

        mockMvc.perform(post("/api/admin/devices/{fid}/revoke", "fid-revoke")
                .header("X-Firebase-Installation-Id", "test-approved-device"))
                .andExpect(status().isOk());

        verify(deviceRegistrationRepository).save(registration);
    }

    @Test
    @WithMockUser(username = "user", roles = {"GESTOR"})
    void revokeDeviceForbiddenForNonAdministrators() throws Exception {
        mockMvc.perform(post("/api/admin/devices/{fid}/revoke", "fid-revoke"))
                .andExpect(status().isForbidden());

        verify(deviceRegistrationRepository, never()).findByFid("fid-revoke");
    }
}
