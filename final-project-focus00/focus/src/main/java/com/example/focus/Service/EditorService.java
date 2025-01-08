package com.example.focus.Service;

import com.example.focus.ApiResponse.ApiException;
import com.example.focus.DTO.EditorDTO;
import com.example.focus.DTO.EditorDTOin;
import com.example.focus.Model.Editor;
import com.example.focus.Model.MyUser;
import com.example.focus.Model.ProfileEditor;
import com.example.focus.Repository.EditorRepository;
import com.example.focus.Repository.MyUserRepository;
import com.example.focus.Repository.ProfileEditorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EditorService {
    private final MyUserRepository myUserRepository;
    private final EditorRepository editorRepository;
    private final ProfileEditorRepository profileEditorRepository;

    public List<EditorDTO> getAllEditors() {
        List<Editor> editors = editorRepository.findAll();
        List<EditorDTO> editorDTOS = new ArrayList<>();

        for (Editor editor : editors) {
            EditorDTO editorDTO = new EditorDTO(
                    editor.getName(),
                    editor.getMyUser().getUsername(),
                    editor.getMyUser().getEmail(),
                    editor.getCity(),
                    editor.getPhoneNumber()
            );
            editorDTOS.add(editorDTO);
        }
        return editorDTOS;
    }

    public void UserRegistration(EditorDTOin editorDTOin) {
        String hashPass = new BCryptPasswordEncoder().encode(editorDTOin.getPassword());

        MyUser user = new MyUser();
        user.setUsername(editorDTOin.getUsername());
        user.setEmail(editorDTOin.getEmail());
        user.setPassword(hashPass);
        user.setRole("EDITOR");


        myUserRepository.save(user);
        EditorRegistration(editorDTOin, user);

    }
    private void EditorRegistration(EditorDTOin editorDTOin,MyUser user) {

        Editor editor = new Editor();

        editor.setName(editorDTOin.getName());
        editor.setCity(editorDTOin.getCity());
        editor.setPhoneNumber(editorDTOin.getPhoneNumber());
        editor.setMyUser(user);
        editorRepository.save(editor);
        EditorProfile(user);

    }
    public void EditorProfile(MyUser newUser) {

        ProfileEditor profileEditor = new ProfileEditor();
        profileEditor.setEditor(editorRepository.findEditorById(newUser.getId()));
        profileEditor.setNumberOfPosts(0);


        profileEditorRepository.save(profileEditor);
    }

    public void updateEditor(Integer userid, EditorDTOin editorDTOin) {
        Editor existingEditor = editorRepository.findEditorById(userid);
        if (existingEditor == null) {
            throw new ApiException("Editor not found");
        }

        existingEditor.setName(editorDTOin.getName());
        existingEditor.setCity(editorDTOin.getCity());
        existingEditor.getMyUser().setUsername(editorDTOin.getUsername());
        existingEditor.getMyUser().setEmail(editorDTOin.getEmail());
        existingEditor.setPhoneNumber(editorDTOin.getPhoneNumber());

        editorRepository.save(existingEditor);
    }

    public void deleteEditor(Integer userid, Integer editorid) {
        MyUser myUser = myUserRepository.findMyUserById(userid);
        Editor editor = editorRepository.findEditorById(editorid);

        if (myUser == null || editor == null) {
            throw new ApiException("User or Editor not found");
        }

        myUserRepository.delete(myUser);
    }
}
