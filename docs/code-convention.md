# 코드 컨벤션

## API 이름 규칙

- 요청·응답 JSON 키와 쿼리 파라미터는 snake_case로 쓴다. 자바 코드의 이름은 camelCase로 쓴다.
- JSON 키는 전역 설정으로 변환되므로 따로 지정하지 않는다.
- 쿼리 파라미터는 이름을 직접 지정한다.
    - 하나씩 받을 때: `@RequestParam("snake_case")`
    - 객체로 묶어 받을 때: `public record`의 각 필드에 `@BindParam("snake_case")`를 붙이고, 컨트롤러 파라미터에 `@ParameterObject`를 붙인다.

```java
public record NearbyPlaceQuery(
        @BindParam("neighborhood_id") Long neighborhoodId,
        @BindParam("station_name") String stationName) {}

@GetMapping("/places")
public List<PlaceResponse> getPlaces(@ParameterObject NearbyPlaceQuery query) { ... }
```

## 에러 처리

- 에러는 예외를 던져서 처리한다. Controller에서 try-catch로 에러 응답을 직접 만들지 않는다.

### 에러 코드

- 여러 기능에서 쓰는 일반적인 에러는 `CommonErrorCode`를 쓴다.
- 특정 기능에만 해당하는 에러는 기능별 enum(예: `PlaceErrorCode`)을 만들어 `ErrorCode`를 구현한다.
- `code()`는 enum 상수 이름(`name()`)을 반환한다. 한번 정한 이름은 바꾸지 않는다.
- `message`는 사용자에게 보여줄 수 있는 문장으로 쓰고, 내부 정보(쿼리, 예외 메시지 등)를 넣지 않는다.
- `CommonErrorCode`에는 HTTP 상태 코드마다 항목을 하나만 둔다.

```java
public enum PlaceErrorCode implements ErrorCode {
    PLACE_NOT_FOUND(HttpStatus.NOT_FOUND, "장소를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String message;

    // 생성자, status(), code() { return name(); }, message() 구현
}
```

### 예외

- `BaseException`을 상속한 예외를 상황마다 만들고, 생성자에서 `ErrorCode`를 넘긴다.

```java
public class PlaceNotFoundException extends BaseException {
    public PlaceNotFoundException() {
        super(PlaceErrorCode.PLACE_NOT_FOUND);
    }
}
```

## 테스트

- 테스트 메서드 이름은 영어 camelCase로 짓고, 무엇을 검증하는지는 `@DisplayName`에 한글 문장으로 적는다.

```java
@Test
@DisplayName("없는 주소로 요청하면 404를 응답한다")
void notFound() throws Exception { ... }
```
