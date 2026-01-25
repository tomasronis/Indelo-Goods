# Supabase Migration - REST API Implementation

## Overview
Migrated from Supabase Kotlin SDK (which had dependency resolution issues) to direct Supabase REST API calls using Retrofit.

## Completed Changes

### 1. Dependencies (app/build.gradle.kts)
- ✅ Removed Supabase SDK dependencies
- ✅ Added Retrofit 2.9.0
- ✅ Added Gson converter
- ✅ Added OkHttp with logging interceptor

### 2. Core API Layer
- ✅ Created `SupabaseApiService.kt` - Retrofit interface for all Supabase REST endpoints
- ✅ Updated `SupabaseClient.kt` - Retrofit client with session management
- ✅ Updated `AuthRepository.kt` - Using REST API for authentication

### 3. API Features Implemented
- ✅ Auth API: Sign up, sign in, OTP (send/verify), password reset, sign out
- ✅ Database API: Generic CRUD operations (select, insert, update, delete)
- ✅ Storage API: File upload/delete endpoints
- ✅ Session Management: Token storage and authentication state

## Updated Files

All repository files have been migrated to use the new REST API:

1. ✅ **AuthRepository.kt** - Authentication (OTP, email/password)
2. ✅ **ProductRepository.kt** - Database operations for products + image upload
3. ✅ **OrderRepository.kt** - Order management
4. ✅ **ShopRepository.kt** - Shop management
5. ✅ **ShopperRepository.kt** - Shopper preferences and subscriptions
6. ✅ **AuthViewModel.kt** - Updated for Boolean session status
7. ✅ **AppNavigation.kt** - Updated for Boolean auth state

## How the REST API Works

### Authentication
```kotlin
// Phone OTP
authRepository.sendOtp("+1234567890")
authRepository.verifyOtp("+1234567890", "123456")

// Email/Password
authRepository.signUp("email@example.com", "password")
authRepository.signIn("email@example.com", "password")
```

### Database Operations
```kotlin
// Select with filters
val filters = mapOf("id" to "eq.${userId}")
api.select(table = "products", filters = filters)

// Insert
api.insert(table = "products", body = product)

// Update
val filters = mapOf("id" to "eq.${productId}")
api.update(table = "products", body = product, filters = filters)

// Delete
val filters = mapOf("id" to "eq.${productId}")
api.delete(table = "products", filters = filters)
```

### PostgREST Filter Operators
- `eq.value` - equals
- `neq.value` - not equals
- `gt.value` - greater than
- `lt.value` - less than
- `like.*value*` - pattern matching
- `ilike.*value*` - case-insensitive pattern matching
- `is.null` - is null
- `in.(val1,val2)` - in list

## Next Steps

1. Update remaining repository files to use REST API
2. Test authentication flow
3. Test database operations
4. Test file upload/download
5. Handle error cases and edge cases

## References

- [Supabase REST API Docs](https://supabase.com/docs/reference/rest)
- [PostgREST API Docs](https://postgrest.org/en/stable/api.html)
- [Supabase Auth API](https://supabase.com/docs/reference/auth)
- [Supabase Storage API](https://supabase.com/docs/reference/storage)
