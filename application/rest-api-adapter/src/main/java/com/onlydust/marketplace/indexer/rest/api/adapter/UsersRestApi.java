package com.onlydust.marketplace.indexer.rest.api.adapter;

import com.onlydust.marketplace.indexer.contract.UsersApi;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tags(@Tag(name = "Users"))
@AllArgsConstructor
public class UsersRestApi implements UsersApi {

}
