package com.example.eventlinkqr;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mockStatic;
import com.google.firebase.database.FirebaseDatabase;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.mock;

public class DatabaseManagerTest {

    @Test
    public void testDatabaseInitialization() {
        try (MockedStatic<FirebaseDatabase> mocked = mockStatic(FirebaseDatabase.class)) {
            FirebaseDatabase mockDatabase = mock(FirebaseDatabase.class);
            mocked.when(FirebaseDatabase::getInstance).thenReturn(mockDatabase);

            DatabaseManager manager = DatabaseManager.getInstance();
            FirebaseDatabase database = manager.getDatabase();

            assertNotNull(database);
            mocked.verify(FirebaseDatabase::getInstance);
        }
    }
}
