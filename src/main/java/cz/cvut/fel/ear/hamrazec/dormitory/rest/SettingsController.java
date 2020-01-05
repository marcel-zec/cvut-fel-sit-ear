package cz.cvut.fel.ear.hamrazec.dormitory.rest;

import cz.cvut.fel.ear.hamrazec.dormitory.exception.BadPassword;
import cz.cvut.fel.ear.hamrazec.dormitory.service.security.AccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/settings")
public class SettingsController {

        private AccessService accessService;

        @Autowired
        public SettingsController(AccessService accessService) {
            this.accessService = accessService;
        }

    @PatchMapping(value = "/password", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updatePassword(@RequestBody HashMap<String,String> hashMap) throws BadPassword {
        String oldPassword = hashMap.get("old_password");
        String newPassword = hashMap.get("new_password");
        String newPasswordAgain = hashMap.get("new_password_again");

        accessService.changePassword(oldPassword,newPassword,newPasswordAgain);
    }
}
