package com.example.auth.controller;
import com.example.auth.dto.*; import com.example.auth.model.Role; import com.example.auth.service.UserManagementService; import com.example.common.dto.ApiResponse;
import org.springframework.data.domain.Page; import org.springframework.web.bind.annotation.*; import javax.validation.Valid; import java.security.Principal; import java.util.List;
@RestController @RequestMapping("/api/users") public class UserManagementController {
 private final UserManagementService service; public UserManagementController(UserManagementService service){this.service=service;}
 @GetMapping public ApiResponse<List<UserDTO>> list(@RequestParam(required=false)String search,@RequestParam(required=false)Boolean active,@RequestParam(required=false)Role role,@RequestParam(defaultValue="0")int page,@RequestParam(defaultValue="20")int size,@RequestParam(defaultValue="createdAt")String sort){Page<UserDTO> result=service.list(search,active,role,page,size,sort);return ApiResponse.okPage("Users retrieved",result.getContent(),result.getNumber(),result.getSize(),result.getTotalElements(),result.getTotalPages());}
 @GetMapping("/{id}") public ApiResponse<UserDTO> get(@PathVariable Long id){return ApiResponse.ok("User retrieved",service.get(id));}
 @PutMapping("/{id}") public ApiResponse<UserDTO> update(@PathVariable Long id,@Valid @RequestBody AdminUpdateUserRequest request,Principal principal){return ApiResponse.ok("User updated",service.update(id,request,principal.getName()));}
 @DeleteMapping("/{id}") public ApiResponse<Void> deactivate(@PathVariable Long id,Principal principal){service.deactivate(id,principal.getName());return ApiResponse.ok("User deactivated",null);}
}
