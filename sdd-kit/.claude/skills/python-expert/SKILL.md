---
name: python-expert
description: Expert skill for Python development with FastAPI, Flask, or Django. Provides patterns for REST APIs, SQLAlchemy, Pydantic, async/await, error handling, and testing. Use when implementing Python features within the SDD workflow. **TRIGGER ON** Python, FastAPI, Flask, Django, SQLAlchemy, Alembic, Pydantic, pytest, async, Poetry, pip.
model: sonnet
---

# Python Expert

You are an expert in Python web development. Your role is to guide implementation within the SDD Kit workflow, providing concrete, production-ready patterns for FastAPI (primary), Flask, or Django.

## Technology Stack

| Layer | Technology | Version |
|-------|-----------|---------|
| Language | Python | 3.11+ |
| Framework | FastAPI (primary) / Flask / Django | latest |
| ORM | SQLAlchemy 2.x | |
| Migrations | Alembic | |
| Validation | Pydantic v2 | |
| Package Manager | Poetry or pip + venv | |
| Testing | pytest + httpx + pytest-asyncio | |
| HTTP Client | httpx | |

---

## Architecture Patterns

### Clean Architecture (FastAPI)

```
src/
├── api/
│   ├── routers/             # FastAPI routers (controllers)
│   └── schemas/             # Pydantic schemas (DTOs)
├── application/
│   └── services/            # Business logic / use cases
├── domain/
│   └── models/              # SQLAlchemy models
├── infrastructure/
│   ├── database.py          # DB session setup
│   └── repositories/        # Repository implementations
└── main.py                  # App factory + router registration
```

### FastAPI App Setup

```python
# main.py
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from src.api.routers import users, auth
from src.infrastructure.database import engine, Base

def create_app() -> FastAPI:
    app = FastAPI(
        title="My API",
        version="1.0.0",
        docs_url="/docs",
        redoc_url="/redoc",
    )

    app.add_middleware(
        CORSMiddleware,
        allow_origins=["*"],
        allow_methods=["*"],
        allow_headers=["*"],
    )

    app.include_router(users.router, prefix="/api/v1/users", tags=["users"])
    app.include_router(auth.router, prefix="/api/v1/auth", tags=["auth"])

    return app

app = create_app()
```

### Router (Controller) Pattern

```python
# api/routers/users.py
from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.ext.asyncio import AsyncSession
from src.infrastructure.database import get_session
from src.application.services.user_service import UserService
from src.api.schemas.user import CreateUserRequest, UpdateUserRequest, UserResponse

router = APIRouter()

def get_user_service(session: AsyncSession = Depends(get_session)) -> UserService:
    return UserService(session)

@router.get("/{user_id}", response_model=UserResponse)
async def get_user(
    user_id: int,
    service: UserService = Depends(get_user_service)
):
    user = await service.find_by_id(user_id)
    if not user:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="User not found")
    return user

@router.post("/", response_model=UserResponse, status_code=status.HTTP_201_CREATED)
async def create_user(
    request: CreateUserRequest,
    service: UserService = Depends(get_user_service)
):
    return await service.create(request)

@router.put("/{user_id}", response_model=UserResponse)
async def update_user(
    user_id: int,
    request: UpdateUserRequest,
    service: UserService = Depends(get_user_service)
):
    user = await service.update(user_id, request)
    if not user:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="User not found")
    return user

@router.delete("/{user_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_user(
    user_id: int,
    service: UserService = Depends(get_user_service)
):
    deleted = await service.delete(user_id)
    if not deleted:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="User not found")
```

### Pydantic Schemas

```python
# api/schemas/user.py
from pydantic import BaseModel, EmailStr, Field
from enum import Enum
from datetime import datetime

class UserRole(str, Enum):
    admin = "admin"
    user = "user"

class CreateUserRequest(BaseModel):
    name: str = Field(..., min_length=2, max_length=100)
    email: EmailStr
    role: UserRole = UserRole.user

class UpdateUserRequest(BaseModel):
    name: str | None = Field(None, min_length=2, max_length=100)
    role: UserRole | None = None

class UserResponse(BaseModel):
    id: int
    name: str
    email: str
    role: UserRole
    created_at: datetime

    model_config = {"from_attributes": True}
```

### SQLAlchemy Model

```python
# domain/models/user.py
from sqlalchemy import String, Enum as SAEnum, func
from sqlalchemy.orm import Mapped, mapped_column
from src.infrastructure.database import Base
from enum import Enum
import datetime

class UserRole(str, Enum):
    admin = "admin"
    user = "user"

class User(Base):
    __tablename__ = "users"

    id: Mapped[int] = mapped_column(primary_key=True, autoincrement=True)
    name: Mapped[str] = mapped_column(String(100), nullable=False)
    email: Mapped[str] = mapped_column(String(255), unique=True, nullable=False)
    role: Mapped[UserRole] = mapped_column(SAEnum(UserRole), nullable=False)
    created_at: Mapped[datetime.datetime] = mapped_column(server_default=func.now())
    updated_at: Mapped[datetime.datetime] = mapped_column(
        server_default=func.now(), onupdate=func.now()
    )
```

### Service Layer

```python
# application/services/user_service.py
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select
from src.domain.models.user import User
from src.api.schemas.user import CreateUserRequest, UpdateUserRequest, UserResponse

class UserService:
    def __init__(self, session: AsyncSession):
        self.session = session

    async def find_by_id(self, user_id: int) -> UserResponse | None:
        result = await self.session.get(User, user_id)
        return UserResponse.model_validate(result) if result else None

    async def create(self, request: CreateUserRequest) -> UserResponse:
        # Check for duplicate email
        stmt = select(User).where(User.email == request.email)
        existing = await self.session.scalar(stmt)
        if existing:
            raise ValueError(f"Email already registered: {request.email}")

        user = User(name=request.name, email=request.email, role=request.role)
        self.session.add(user)
        await self.session.commit()
        await self.session.refresh(user)
        return UserResponse.model_validate(user)

    async def update(self, user_id: int, request: UpdateUserRequest) -> UserResponse | None:
        user = await self.session.get(User, user_id)
        if not user:
            return None
        if request.name is not None:
            user.name = request.name
        if request.role is not None:
            user.role = request.role
        await self.session.commit()
        await self.session.refresh(user)
        return UserResponse.model_validate(user)

    async def delete(self, user_id: int) -> bool:
        user = await self.session.get(User, user_id)
        if not user:
            return False
        await self.session.delete(user)
        await self.session.commit()
        return True
```

### Database Session

```python
# infrastructure/database.py
from sqlalchemy.ext.asyncio import create_async_engine, AsyncSession, async_sessionmaker
from sqlalchemy.orm import DeclarativeBase
import os

DATABASE_URL = os.environ["DATABASE_URL"]

engine = create_async_engine(DATABASE_URL, echo=False)
AsyncSessionLocal = async_sessionmaker(engine, expire_on_commit=False)

class Base(DeclarativeBase):
    pass

async def get_session():
    async with AsyncSessionLocal() as session:
        yield session
```

---

## Error Handling

### Global Exception Handler

```python
# api/exception_handlers.py
from fastapi import Request
from fastapi.responses import JSONResponse
from fastapi.exceptions import RequestValidationError

async def validation_exception_handler(request: Request, exc: RequestValidationError):
    errors = [{"field": e["loc"][-1], "message": e["msg"]} for e in exc.errors()]
    return JSONResponse(
        status_code=422,
        content={"error": "VALIDATION_ERROR", "details": errors}
    )

async def value_error_handler(request: Request, exc: ValueError):
    return JSONResponse(
        status_code=422,
        content={"error": "BUSINESS_ERROR", "message": str(exc)}
    )

# Register in create_app():
# app.add_exception_handler(RequestValidationError, validation_exception_handler)
# app.add_exception_handler(ValueError, value_error_handler)
```

---

## Database Migrations (Alembic)

```bash
# Initialize Alembic
alembic init alembic

# Generate migration
alembic revision --autogenerate -m "create users table"

# Apply migrations
alembic upgrade head
```

```python
# alembic/env.py (key config)
from src.domain.models.user import Base  # noqa: F401 — import all models
target_metadata = Base.metadata
```

---

## Testing Patterns

### Unit Test (Service)

```python
# tests/unit/test_user_service.py
import pytest
from unittest.mock import AsyncMock, MagicMock
from src.application.services.user_service import UserService
from src.api.schemas.user import CreateUserRequest, UserRole

@pytest.fixture
def mock_session():
    session = AsyncMock()
    session.scalar = AsyncMock(return_value=None)  # no duplicate email
    return session

@pytest.mark.asyncio
async def test_create_user_success(mock_session):
    service = UserService(mock_session)
    request = CreateUserRequest(name="John", email="john@example.com", role=UserRole.user)

    mock_session.refresh = AsyncMock()
    result_user = MagicMock(id=1, name="John", email="john@example.com",
                            role=UserRole.user, created_at=None)
    mock_session.get = AsyncMock(return_value=result_user)

    # Verify no exception raised
    mock_session.commit = AsyncMock()
    mock_session.add = MagicMock()

    # Service creates and commits
    await service.create(request)
    mock_session.add.assert_called_once()
    mock_session.commit.assert_called_once()
```

### Integration Test (FastAPI TestClient)

```python
# tests/integration/test_users_api.py
import pytest
from httpx import AsyncClient, ASGITransport
from src.main import app

@pytest.mark.asyncio
async def test_create_and_get_user():
    async with AsyncClient(
        transport=ASGITransport(app=app), base_url="http://test"
    ) as client:
        # Create
        response = await client.post("/api/v1/users/", json={
            "name": "Alice",
            "email": "alice@example.com",
            "role": "user"
        })
        assert response.status_code == 201
        user_id = response.json()["id"]

        # Get
        response = await client.get(f"/api/v1/users/{user_id}")
        assert response.status_code == 200
        assert response.json()["name"] == "Alice"

@pytest.mark.asyncio
async def test_create_user_with_invalid_email_returns_422():
    async with AsyncClient(
        transport=ASGITransport(app=app), base_url="http://test"
    ) as client:
        response = await client.post("/api/v1/users/", json={
            "name": "Bob",
            "email": "not-an-email",
            "role": "user"
        })
        assert response.status_code == 422
```

---

## pyproject.toml (Poetry)

```toml
[tool.poetry]
name = "my-api"
version = "0.1.0"
description = ""
authors = ["Your Name <you@example.com>"]

[tool.poetry.dependencies]
python = "^3.11"
fastapi = "^0.111"
uvicorn = {extras = ["standard"], version = "^0.29"}
sqlalchemy = {extras = ["asyncio"], version = "^2.0"}
alembic = "^1.13"
pydantic = {extras = ["email"], version = "^2.0"}
asyncpg = "^0.29"      # PostgreSQL async driver
python-dotenv = "^1.0"

[tool.poetry.group.dev.dependencies]
pytest = "^8.0"
pytest-asyncio = "^0.23"
httpx = "^0.27"
anyio = {extras = ["trio"], version = "^4.0"}
```

---

## How to Use This Skill

When implementing a Python feature in the SDD workflow:

1. **Reference this skill** when writing technical specs for Python services
2. **Choose FastAPI** for new greenfield APIs; use Flask/Django for existing projects
3. **Always use type hints** — Pydantic + SQLAlchemy 2.x mapped_column style
4. **Write async by default** — FastAPI + asyncio is the modern standard
5. **Use these test patterns** in `sdd-small-test-writer` tasks

**Integrates with**: `sdd-implementer`, `sdd-system-designer`, `sdd-small-test-writer`
