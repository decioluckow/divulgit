package org.divulgit.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProjectState {
    NEW, ACTIVE, IGNORED;
}
