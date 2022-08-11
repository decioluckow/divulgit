package org.divulgit.azure.thread;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AzureThread  {

    private String id;

    private List<AzureComment> comments;

    private ThreadContext threadContext;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ThreadContext {
        private String filePath;
    }
}
