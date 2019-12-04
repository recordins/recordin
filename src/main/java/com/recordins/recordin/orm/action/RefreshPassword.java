/*
 * Record'in
 *
 * Copyright (C) 2019 Blockchain Record'in Solutions
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.recordins.recordin.orm.action;

import com.recordins.recordin.orm.BlockchainObject;
import com.recordins.recordin.orm.User;
import com.recordins.recordin.orm.core.BlockchainIndex;
import com.recordins.recordin.orm.core.BlockchainObjectIndex;
import com.recordins.recordin.orm.core.BlockchainObjectReader;
import com.recordins.recordin.orm.core.BlockchainObjectWriter;
import com.recordins.recordin.orm.exception.ORMException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class RefreshPassword implements Action {

    private static Logger logger = LoggerFactory.getLogger(RefreshPassword.class);

    ArrayList<String> arrayUID = new ArrayList();

    private RefreshPassword() {
    }

    public RefreshPassword(String args) {
        logger.trace("START: RefreshPassword(ArrayList)");

        JSONParser parser = new JSONParser();
        try {
            JSONObject jsonArgs = (JSONObject) parser.parse(args);
            JSONArray jsonArrayUID = (JSONArray) jsonArgs.get("ids");

            this.arrayUID = new ArrayList(jsonArrayUID);

            for (String uid : arrayUID) {
                logger.debug("UID: " + uid);
            }

        } catch (ParseException ex) {
            logger.error("Error parsing Action arguments: " + ex.toString());
        }

        logger.trace("END: RefreshPassword()");
    }

    @Override
    public void execute(User user) throws ORMException {
        logger.trace("START: execute(User)");

        BlockchainObjectReader reader = BlockchainObjectReader.getInstance(user);
        BlockchainObjectWriter writer = BlockchainObjectWriter.getInstance(user);
        BlockchainIndex<String, String> currentVersionsIndex = BlockchainObjectIndex.getInstance().getIndex(BlockchainObjectIndex.INDEX_TYPE.CURRENT_VERSIONS);

        for (String uid : arrayUID) {

            if (currentVersionsIndex.containsKey(uid)) {
                String id = currentVersionsIndex.get(uid);

                BlockchainObject object = reader.read(id);
                User.readKeyAndCredentials(object, User.getAdminUser());
            }
        }

        logger.trace("END: execute()");
    }
}
