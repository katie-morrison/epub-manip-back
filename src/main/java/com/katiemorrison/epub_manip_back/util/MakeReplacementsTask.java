package com.katiemorrison.epub_manip_back.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.katiemorrison.epub_manip_back.model.pojos.PhraseReplacements;
import com.katiemorrison.epub_manip_back.util.ReplacementProcessor.BeforeAfterProcessor;

public class MakeReplacementsTask implements Runnable {
    List<PhraseReplacements> replacements;
    Path path;

    public MakeReplacementsTask(List<PhraseReplacements> replacements, Path path) {
        this.replacements = replacements;
        this.path = path;
    }

    @Override
    public void run() {
        if (replacements.size() > 0) {
            try {
                String fileContent = Files.readString(path);
                Pattern bodyPattern = Pattern.compile("(<body>)(.*?)(</body>)", Pattern.DOTALL);
                Matcher bodyMatcher = bodyPattern.matcher(fileContent);
                if (bodyMatcher.find()) {
                    String body = bodyMatcher.group(2);

                    for (PhraseReplacements replacement : replacements) {
                        BeforeAfterProcessor processor = new BeforeAfterProcessor(replacement);
                        body = FileUtils.processReplacements(body, "(>)([^<]*+)(<)", 2, processor);
                    }

                    fileContent = fileContent.replace(bodyMatcher.group(0), bodyMatcher.group(1) + body + bodyMatcher.group(3));
                }

                Files.writeString(path, fileContent, StandardOpenOption.TRUNCATE_EXISTING);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
