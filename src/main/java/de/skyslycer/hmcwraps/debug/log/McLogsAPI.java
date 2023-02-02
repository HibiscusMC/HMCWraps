package de.skyslycer.hmcwraps.debug.log;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

/**
 * Copyright (c) 2021-2023 Aternos GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
public class McLogsAPI {

    private static final String API_HOST = "api.mclo.gs";
    private static final String PROTOCOL = "https";

    public static String mcVersion = "unknown";
    public static String userAgent = "unknown";
    public static String version = "unknown";

    /**
     * share a log to the mclogs API
     * @param log The {@link MinecraftLog} to share
     * @return mclogs {@link McLogsResponse}
     * @throws IOException error sharing the log content
     */
    public static McLogsResponse share(MinecraftLog log) throws Exception {
        var client = HttpClient.newHttpClient();
        var request = client.send(HttpRequest.newBuilder()
                .uri(URI.create(PROTOCOL + "://" + API_HOST + "/1/log"))
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .header("User-Agent", userAgent + "/" + version + "/" + mcVersion)
                .POST(HttpRequest.BodyPublishers.ofString("content=" + URLEncoder.encode(log.getContent(), StandardCharsets.UTF_8)))
                .build(), HttpResponse.BodyHandlers.ofString());
        return McLogsResponse.parse(request.body());
    }

    /**
     * share a log to the mclogs API
     * @param logFile The log file, which must have a file extension of '.log'. The file extension may be suffixed by both `.0` and `.gz`
     * @return mclogs {@link McLogsResponse}
     * @throws IOException error reading/sharing file
     */
    public static McLogsResponse share(Path logFile) throws Exception {
        return share(new MinecraftLog(logFile));
    }

}
