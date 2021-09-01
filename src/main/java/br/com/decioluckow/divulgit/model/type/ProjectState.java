package br.com.decioluckow.divulgit.model.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProjectState {
    NEW, ACTIVE, IGNORED;
}
